package cz;

import au.com.bytecode.opencsv.CSVReader;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

/** Class to transform data from csv to xml
 * @author Vitezslav Tkac
 */
public class Main {

  public static void main( String[] args ) throws IOException, ParserConfigurationException, TransformerException {

    // loading particular arguments
    File                       input          = new File( args[0] );
    String                     inputFormat    = args[1];
    File                       categoryFolder = new File( args[2] );
    File                       output         = new File( args[3] );
    long                       id             = Long.valueOf( args[4] );
    String                     encoding       = args[5];
    Map<String, CategoryGroup> categoryGroups = new HashMap<>();

// building category groups
// for each file in folder
    for (File categoryFile : categoryFolder.listFiles()) {
      String        categoryName  = null;
      CategoryGroup categoryGroup = new CategoryGroup();

      // load file a category configuration file into csv reader
      CSVReader inputReader = new CSVReader( new InputStreamReader( new FileInputStream( categoryFile ),
                                                                    encoding ) );
      HashMap<String, String> categories = new HashMap<>();
      int                     i          = 0;
      // for each row in csv file
      for (String[] row : inputReader.readAll()) {
        // if current row is the first then fullfill the category name and code
        if (i == 0) {
          categoryName = row[0];
          categoryGroup.setCode( row[1] );
        } else {
          // otherwise add a new category
          categories.put( row[0], row[1] );
        }
        i++;
      }
      categoryGroup.setCategories( categories );
      categoryGroups.put( categoryName, categoryGroup );
      System.out.println( "Category " + categoryGroup.getCode() + " loaded " + categories.size() );

    }

    // load file an input file into csv reader
    CSVReader inputReader = new CSVReader( new InputStreamReader( new FileInputStream( input ), encoding ) );

    List<Swimmer> swimmers = null;
    // here is necessary to decide, which format will be processed am/pm
    if (inputFormat.toLowerCase().trim().equals( "am" )) {
      swimmers = processAmSwimmerFormat( inputReader.readAll(), id, categoryGroups );
    } else if (inputFormat.toLowerCase().trim().equals( "pm" )) {
      swimmers = processPmSwimmerFormat( inputReader.readAll(), id, categoryGroups );
    } else {
      throw new RuntimeException( "Input format isn't set." );
    }
    System.out.println( "Swimmers created: " + swimmers.size() );

    // create a new xml document object
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder        builder = factory.newDocumentBuilder();
    Document               doc     = builder.newDocument();
    Element                teamEl  = doc.createElement( "team" );
    doc.appendChild( teamEl );

    // for each swimmer object build a new xml node
    for (Swimmer swimmer : swimmers) {
      Element swimmerEl = doc.createElement( "swimmer" );
      swimmerEl.setAttribute( "id", String.valueOf( swimmer.getId() ) );
      swimmerEl.setAttribute( "name", swimmer.getName() + " " + swimmer.getSurname() );
      swimmerEl.setAttribute( "catYear", swimmer.getCatYear() );
      swimmerEl.setAttribute( "sex", swimmer.getGender() );
      for (Map.Entry<String, String> category : swimmer.getCategories().entrySet()) {
        Element categoryEl = doc.createElement( "swdics" );
        categoryEl.setAttribute( "discNo", category.getKey() );
        if (category.getValue() != null) {
          categoryEl.setAttribute( "startTime", category.getValue() );
        }
        swimmerEl.appendChild( categoryEl );
      }
      teamEl.appendChild( swimmerEl );
    }

    // preparation the transformation objects from xml object into stream
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer        transformer        = transformerFactory.newTransformer();
    transformer.setOutputProperty( OutputKeys.INDENT, "yes" );

    // define the source
    DOMSource    source = new DOMSource( doc );
    // result will be into stream
    StreamResult result = new StreamResult( output );

    // do the transformation
    transformer.transform( source, result );
    System.out.println( "Output created." );
  }

  /** Method to process csv data in pm format into {@link Swimmer} objects
   * @param data - list of swimmer data
   * @param id - start id index
   * @param categoryGroups - category groups to define category code
   * @return list of swimmers
   */
  private static List<Swimmer> processPmSwimmerFormat( List<String[]> data,
                                                       long id,
                                                       Map<String, CategoryGroup> categoryGroups ) {
    List<Swimmer> swimmers = new ArrayList<>();
    for (String[] row : data) {
      Swimmer swimmer = new Swimmer();
      swimmer.setId( id );
      swimmer.setSurname( row[3] );
      swimmer.setName( row[4] );
      // get the category code by the name
      swimmer.setGender( categoryGroups.get( row[2] ).getCode() );
      swimmer.setCatYear( row[6] );

      swimmer.setCategories( new HashMap<>() );
      // put the cols 6 - 69 into iterator and get the category code and start time
      Iterator<String> iterator = Arrays.asList( Arrays.copyOfRange( row, 7, 118 ) ).iterator();
      // loop while a next value exists
      while (iterator.hasNext()) {
        String categoryCode = null;
        String startTime    = null;
        // get the next value from iterator - category name
        String category     = iterator.next().trim();

        // if category isn't empty then get the category code
        if (!category.isEmpty()) {
          categoryCode = categoryGroups.get( row[1] ).getCategories().get( category );
          if (categoryCode == null) {
            System.err.println( "Category not found: " + category );
          }
        }

        // get the next value from iterator if the next value exists - start time
        if (iterator.hasNext()) {
          startTime = iterator.next();
        }
        if (!category.isEmpty()) {
          swimmer.getCategories().put( categoryCode, startTime );
        }
      }

      id++;
      swimmers.add( swimmer );
    }
    return swimmers;
  }

  /** Method to process csv data in am format into {@link Swimmer} objects
   * @param data - list of swimmer data
   * @param id - start id index
   * @param categoryGroups - category groups to define category code
   * @return list of swimmers
   */
  private static List<Swimmer> processAmSwimmerFormat( List<String[]> data, long id, Map<String, CategoryGroup>
          categoryGroups ) {

    List<Swimmer> swimmers = new ArrayList<>();
    for (String[] row : data) {
      Swimmer swimmer = new Swimmer();
      swimmer.setId( id );
      swimmer.setSurname( row[3] );
      swimmer.setName( row[4] );
      swimmer.setGender( categoryGroups.get( row[2] ).getCode() );
      // get a category year from cols 6 - 9. only the first non-empty value is taken
      for (String catYear : Arrays.copyOfRange( row, 7, 9 )) {
        swimmer.setCatYear( catYear );
        if (!catYear.isEmpty()) {
          swimmer.setCatYear( catYear );
          break;
        }
      }

      swimmer.setCategories( new HashMap<>() );
      Iterator<String> iterator = Arrays.asList( Arrays.copyOfRange( row, 9, 29 ) ).iterator();
      while (iterator.hasNext()) {
        String categoryCode = null;
        String startTime    = null;
        String category     = iterator.next().trim();

        if (!category.isEmpty()) {
          categoryCode = categoryGroups.get( row[1] ).getCategories().get( category );
          if (categoryCode == null) {
            System.err.println( "Category not found: " + category );
          }
        }

        if (iterator.hasNext()) {
          startTime = iterator.next();
        }
        if (!category.isEmpty()) {
          swimmer.getCategories().put( categoryCode, startTime );
        }
      }

      id++;
      swimmers.add( swimmer );
    }
    return swimmers;
  }


}
