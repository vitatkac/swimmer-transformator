package cz;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MainTest {

  @Test
  public void amTest() throws Exception {

    File output = File.createTempFile("amSwimmerTestOutput","xml");


    Main.main( new String[]{MainTest.class.getResource( "/am.csv" ).getFile().toString(), "am",
                       MainTest.class.getResource( "/categories" ).getFile().toString(),
                       output.getAbsolutePath(), "0", "UTF-8"});

  }

  @Test
  public void pmTest() throws Exception {

    File output = File.createTempFile( "pmSwimmerTestOutput", "xml" );


    Main.main( new String[]{MainTest.class.getResource( "/pm.csv" ).getFile().toString(), "pm",
            MainTest.class.getResource( "/categories" ).getFile().toString(),
            output.getAbsolutePath(), "0", "UTF-8"} );

  }

}