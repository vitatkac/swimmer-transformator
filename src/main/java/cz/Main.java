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

/**
 * Created by vtkac on 25.02.2017.
 */
public class Main {

  public static void main( String[] args ) throws IOException, ParserConfigurationException, TransformerException {

    File                       input          = new File( args[0] );
    String                     inputFormat    = args[1];
    File                       categoryFolder = new File( args[2] );
    File                       output         = new File( args[3] );
    long                       id             = Long.valueOf( args[4] );
    String                     encoding       = args[5];
    Map<String, CategoryGroup> categoryGroups = new HashMap<>();

    for (File categoryFile : categoryFolder.listFiles()) {
      String        categoryName  = null;
      CategoryGroup categoryGroup = new CategoryGroup();

      CSVReader inputReader = new CSVReader( new InputStreamReader( new FileInputStream( categoryFile ),
                                                                    encoding ) );
      HashMap<String, String> categories = new HashMap<>();
      int                     i          = 0;
      for (String[] row : inputReader.readAll()) {
        if (i == 0) {
          categoryName = row[0];
          categoryGroup.setCode( row[1] );
        } else {
          categories.put( row[0], row[1] );
        }
        i++;
      }
      categoryGroup.setCategories( categories );
      categoryGroups.put( categoryName, categoryGroup );
      System.out.println( "Category " + categoryGroup.getCode() + " loaded " + categories.size() );

    }

    CSVReader inputReader = new CSVReader( new InputStreamReader( new FileInputStream( input ), encoding ) );

    List<Swimmer> swimmers = null;
    if (inputFormat.equals( "AM" )) {
      processAmSwimmerFormat( inputReader.readAll(),id,categoryGroups );
    } else if (inputFormat.equals( "PM" )) {
      processPmSwimmerFormat( inputReader.readAll(),id,categoryGroups );
    } else {
      throw new RuntimeException( "Input format isn't set." );
    }
    System.out.println( "Swimmers created: " + swimmers.size() );

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder        builder = factory.newDocumentBuilder();
    Document               doc     = builder.newDocument();
    Element                teamEl  = doc.createElement( "team" );
    doc.appendChild( teamEl );

    for (Swimmer swimmer : swimmers) {
      Element swimmerEl = doc.createElement( "swimmer" );
      swimmerEl.setAttribute( "id", String.valueOf( swimmer.getId() ) );
      swimmerEl.setAttribute( "name", swimmer.getName() );
      swimmerEl.setAttribute( "catYear", "" );
      swimmerEl.setAttribute( "sex", swimmer.getGender() );
      for (String category : swimmer.getCategories()) {
        Element categoryEl = doc.createElement( "swdics" );
        categoryEl.setAttribute( "discNo", category );
        swimmerEl.appendChild( categoryEl );
      }
      teamEl.appendChild( swimmerEl );
    }

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer        transformer        = transformerFactory.newTransformer();
    transformer.setOutputProperty( OutputKeys.INDENT, "yes" );

    DOMSource    source = new DOMSource( doc );
    StreamResult result = new StreamResult( output );
    transformer.transform( source, result );
    System.out.println( "Output created." );
  }

  private static List<Swimmer> processPmSwimmerFormat( List<String[]> strings,
                                              long id,
                                              Map<String, CategoryGroup> categoryGroups ) {
    return null;
  }

  private static List<Swimmer> processAmSwimmerFormat( List<String[]> data, long id, Map<String, CategoryGroup>
          categoryGroups){

    List<Swimmer> swimmers = new ArrayList<>();
      for (String[] row : data) {
        Swimmer swimmer = new Swimmer();
        swimmer.setId( id );
        swimmer.setSurname( row[2] );
        swimmer.setName( row[3] );
        swimmer.setGender( categoryGroups.get( row[1] ).getCode() );

        for (String catYear : Arrays.copyOfRange( row, 6, 8 )) {
          if (!catYear.isEmpty()) {
            swimmer.setCatYear( catYear );
            break;
          }
        }

        swimmer.setCategories( new ArrayList<String>() );
        Iterator<String> iterator = Arrays.asList(  Arrays.copyOfRange( row, 9, 54 )).iterator();
        while(iterator.hasNext()){
          String category = iterator.next();
          if (!category.isEmpty()) {
            String categoryCode = categoryGroups.get( row[1] ).getCategories().get( category );
            if (categoryCode != null) {
              swimmer.getCategories().add( categoryCode );
            } else {
              swimmer.getCategories().add( "" );
              System.err.println( "Category not found: " + category );
            }
          }

          if(iterator.hasNext()){
            iterator.next();
          }else{
            break;
          }
        }

        id++;
        swimmers.add( swimmer );
      }
      return swimmers;
  }


}
