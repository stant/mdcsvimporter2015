/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moneydance.modules.features.mdcsvimporter;

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
public class CSVReaderTest
{
   public CSVReaderTest()
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
      CSVReader cvsReader = new CSVReader( data );
      doTest1( cvsReader );
   }

   private void doTest1( CSVReader reader )
      throws IOException
   {
      assertNull( reader.nextField() );
      assertTrue( reader.nextLine() );
      assertEquals( reader.nextField(), "Column 1" );
      assertEquals( reader.nextField(), "Column 2" );
      assertNull( reader.nextField() );
      assertTrue( reader.nextLine() );
      assertEquals( reader.nextField(), "value 11" );
      assertEquals( reader.nextField(), "value 12" );
      assertNull( reader.nextField() );
      assertTrue( reader.nextLine() );
      assertEquals( reader.nextField(), "value 21" );
      assertEquals( reader.nextField(), "value 22" );
      assertNull( reader.nextField() );
      assertFalse( reader.nextLine() );
      assertNull( reader.nextField() );
   }
}