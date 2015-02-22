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
public class CustomReaderDialogTest
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

   public CustomReaderDialogTest()
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
//    main1.init();
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
   @Ignore
   public void expectDateProblem()
      throws IOException
   {
      URL url = MainTest.class.getResource( "aa-test.csv" );
      System.out.println( "url filepath =" + url.getFile() + "=" );

      String testUri = ImportDialog.RUN_ARGS_FILE + "=" + url.getFile()
      //String testUri = ImportDialog.RUN_ARGS_FILE + "=./aa-test.csv"
                + "&fileformat=eu date test"
                + "&importaccount=IMPORT BANK"
                + "&importtype=online&JUNITFLAG2"
                        ;
       
    main1.invoke( testUri );
    
   }
   
//   @Test
   @Ignore
   public void dummySoItHasOneTest()
   {
       
   }
   
}