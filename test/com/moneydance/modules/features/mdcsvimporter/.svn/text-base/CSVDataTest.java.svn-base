/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moneydance.modules.features.mdcsvimporter;

import com.moneydance.apps.md.model.Account;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author miki
 */
public class CSVDataTest
{
   public CSVDataTest()
   {
   }

   @BeforeClass
   public static void setUpClass()
      throws Exception
   {
   }

   @AfterClass
   public static void tearDownClass()
      throws Exception
   {
   }

   @Before
   public void setUp()
   {
   }

   @After
   public void tearDown()
   {
   }
   
   private final String test1 = "\"Column 1\",\"Column 2\"\n\"value 11\",\"value 12\"\n" +
      "\"value 21\",\"value 22\"";

   @Test
   public void noopTest()
      throws IOException
   {
      System.out.println( "finished noopTest() test." );
   }

//   @Test
   @Ignore
   public void simpleTest1()
      throws IOException
   {
      StringReader data = new StringReader( test1 );
      CSVReader csvReader = new CSVReader( data );
      CSVData csvData = new CSVData( csvReader );

      csvData.parseIntoLines( ',' );

      System.out.println( "finished transReader.parse" );
      
      doTest1( csvData );
      csvReader.close();
   }

   private void doTest1( CSVData reader )
      throws IOException
   {
      assertFalse( reader.nextField() );
      assertTrue( reader.nextLine() );
      assertTrue( reader.nextField() );
      assertEquals( reader.getField(), "Column 1" );
      assertTrue( reader.nextField() );
      assertEquals( reader.getField(), "Column 2" );
      assertFalse( reader.nextField() );
      assertTrue( reader.nextLine() );
      assertTrue( reader.nextField() );
      assertEquals( reader.getField(), "value 11" );
      assertTrue( reader.nextField() );
      assertEquals( reader.getField(), "value 12" );
      assertFalse( reader.nextField() );
      assertTrue( reader.nextLine() );
      assertTrue( reader.nextField() );
      assertEquals( reader.getField(), "value 21" );
      assertTrue( reader.nextField() );
      assertEquals( reader.getField(), "value 22" );
      assertFalse( reader.nextField() );
      assertFalse( reader.nextLine() );
      assertFalse( reader.nextField() );

   }
}