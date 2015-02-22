/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moneydance.modules.features.mdcsvimporter;

import java.net.URL;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stan Towianski
 * NOTE: Cannot test everything I want to because I cannot get FeatureModuleContext() because
 * I do not know how to 'start' Moneydance itself, so I cannot get account lists or anything.
 */
public class MainTest
{
   Main main1 = new Main();
   //MoneydanceGUI mdgui = new MoneydanceGUI();
   /*
   FeatureModuleContext context = new FeatureModuleContext() {

        public RootAccount getRootAccount() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getVersion() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getBuild() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void showURL(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void registerFeature(FeatureModule fm, String string, Image image, String string1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void registerHomePageView(FeatureModule fm, HomePageView hpv) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void registerAccountEditor(FeatureModule fm, int i, AccountEditor ae) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
   */

   public MainTest()
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
    main1.init();
   }

   @After
   public void tearDown()
   {
   }

   @Test
   public void noopTest()
      throws IOException
   {
      System.out.println( "finished noopTest() test." );
   }

   /**
    * Test
    */
//   @Test
   @Ignore
   public void expectInvalidFileAndImportaccount()
      throws IOException
   {
       String testUri = ImportDialog.RUN_ARGS_FILE + "=/zzz111222qqq.csv"
                + "&fileformat=Discover Card"
                + "&importaccount=IMPORT BANK"
                + "&deletecsvfileflag"
                + "&importtype=online&JUNITFLAG&processFlag"
                        ;
       
    main1.invoke( testUri );
    
    assertEquals( (Integer) main1.getErrCodeList().get( 0 ), (Integer) ImportDialog.RUN_ARGS_ERRORCODE_INVALID_FILE );
    assertEquals( (Integer) main1.getErrCodeList().get( 1 ), (Integer) ImportDialog.RUN_ARGS_ERRORCODE_INVALID_IMPORTACCOUNT );
   }

   @Ignore
   public void expectInvalidFileformat()
      throws IOException
   {
//      Reader file = new InputStreamReader(
//         MainTest.class.getResourceAsStream( "dateGuesser.csv" ) );

      URL url = MainTest.class.getResource( "dateGuesser.csv" );
      System.out.println( "url filepath =" + url.getFile() + "=" );

      String testUri = ImportDialog.RUN_ARGS_FILE + "=" + url.getFile()
                + "&fileformat=Discover Card"
                + "&deletecsvfileflag"
                + "&importtype=online&JUNITFLAG&processFlag"
                        ;
       
    main1.invoke( testUri );
    
    assertEquals( (Integer) main1.getErrCodeList().get( 0 ), (Integer) ImportDialog.RUN_ARGS_ERRORCODE_INVALID_FILEFORMAT_FOR_FILE );
   }
   
//   @Test
   @Ignore
   public void expectMissingFileArg()
      throws IOException
   {
      String testUri = "importaccount=1232zxzcx"
                + "&deletecsvfileflag"
                + "&importtype=online&JUNITFLAG&processFlag"
                        ;
       
    main1.invoke( testUri );
    
    assertEquals( (Integer) main1.getErrCodeList().get( 0 ), (Integer) ImportDialog.RUN_ARGS_ERRORCODE_REQUIRES_FILE );
   }
   
}