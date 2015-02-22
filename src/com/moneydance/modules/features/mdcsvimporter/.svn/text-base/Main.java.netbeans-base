/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.moneydance.modules.features.mdcsvimporter;

import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.model.RootAccount;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author miki
 */
public class Main
   extends FeatureModule
{
   private static final int VERSION = 18;
   protected static final String VERSION_STRING = " Beta 18";
   private static final String NAME = "CSV Importer";
   private static final String VENDOR = "Stan Towianski, Milutin Jovanović";
   private static final String URL = "http://code.google.com/p/mdcsvimporter/";
   private static final String DESCRIPTION =
      "Let's you create configs for say: Discover card, VISA, your private bank, etc... " +
      "You denote columns like: -Payment-, -Deposit-, date, amount, memo, etc... " +
      "It can test your file, giving you a list of all the readers that can handle your file. " +
      "Importing does matching to skip duplicate entries."
           ;
   private static Image image;

   private ArrayList<Integer> errCodeList = null;

   {
      try
      {
         image = ImageIO.read(
            Main.class.getResourceAsStream( "import.png" ) );
      }
      catch ( IOException x )
      {
         // ignore error; nothing we can do about it
      }
   }

   public Main()
   {
   }
   
   /*
    public static void main(String args[]) 
    {
       String amt = "($157.86)";
       System.out.println( "converted amount =" + amt.replaceAll( "\\((.*)\\)", "-$1" ) );

       amt = "$123.86";
       System.out.println( "converted amount =" + amt.replaceAll( "\\((.*)\\)", "-$1" ) );
   }
    */
   
   @Override
   public void init()
   {
      System.err.println( "name and version =" + NAME + " " + VERSION_STRING + "=" );
      FeatureModuleContext context = getContext();
      if ( context == null )
      {
         System.err.println( "*** Error: got context == null" );
         return;
      }

      context.registerFeature( this, "import", image, "Import File" );
   }

   RootAccount getRootAccount()
   {
      FeatureModuleContext context = getContext();
      return context.getRootAccount();
   }

   JFrame getMoneydanceWindow()
   {
      // Using undocumented feature. This way our windows and dialogs can have a parent,
      // and behave more conformingly. Alternative is just returning null. Effects should
      // be minor visual inconsistencies.

      FeatureModuleContext context = getContext();
      com.moneydance.apps.md.controller.Main main =
         (com.moneydance.apps.md.controller.Main) context;
      if ( main == null )
      {
         return null;
      }
      com.moneydance.apps.md.view.gui.MoneydanceGUI gui =
         (com.moneydance.apps.md.view.gui.MoneydanceGUI) main.getUI();
      if ( gui == null )
      {
         return null;
      }
      return gui.getTopLevelFrame();
   }

   @Override
   public String getName()
   {
      return NAME;
   }

   @Override
   public int getBuild()
   {
      return VERSION;
   }

   @Override
   public String getDescription()
   {
      return DESCRIPTION;
   }

   @Override
   public void invoke( String uri )
   {
       /*
       uri = ImportDialog.RUN_ARGS_FILE + "=/home/aaa/Downloads/aa-test.csv"
                + "&fileformat=Discover Card"
                + "&importaccount=IMPORT BANK"
                + "&deletecsvfileflag"
                + "&importtype=online"
                        ;
        */

      /*
    argsHM.put( "file", "/home/aaa/Downloads/aa-test.csv" );
    argsHM.put( "fileformat", "Discover Card" );
    //argsHM.put( "dateformat", "MM/DD/YYYY" );
    argsHM.put( "importaccount", "IMPORT BANK" );
    argsHM.put( "importtype", "online" );
    argsHM.put( "deletecsvfileflag", null );
      */

      StringTokenizer tokenizer = new StringTokenizer( uri, "&" );
      HashMap argsHM = new HashMap();
      
      //filename="file"&fileformat="file format"&dateformat="date format"&importaccount="my account"
      //deletecsvfileflag&importtype="online|regular"
        
      //int count = tokenizer.countTokens();
      //String url = count + " tokens(";
      System.err.println( "uri string =" + uri + "=" );

      while ( tokenizer.hasMoreTokens() )
          {
         //url = url.concat( tokenizer.nextToken() );
          String [] pcs = tokenizer.nextToken().split( "=" );
          System.err.println( "arg token [0] =" + pcs[0] + "=   token[1] =" + (pcs.length < 2 ?  "" : pcs[1]) + "=" );
          if ( pcs.length > 1 )
              {
              if ( pcs[1].startsWith( "\"" ) )
                  {
                  argsHM.put( pcs[0].toLowerCase(),  pcs[1].substring( 1, pcs[1].length() - 1 ) );
                  System.err.println( "arg key =" + pcs[0].toLowerCase() + "=   value =" + pcs[1].substring( 1, pcs[1].length() - 1 ) + "=" );
                  }
              else
                  {
                  argsHM.put( pcs[0].toLowerCase(),  pcs[1] );
                  System.err.println( "arg key =" + pcs[0].toLowerCase() + "=   value =" + pcs[1] + "=" );
                  }
              }
          else
              {
              argsHM.put( pcs[0].toLowerCase(),  null );
              System.err.println( "arg key =" + pcs[0].toLowerCase() + "=   value =" + null + "=" );
              }
          }
      argsHM.remove( "import" );  // This seems to be passed in and I do not know why.
    
      ImportDialog dialog = new ImportDialog( this, argsHM );

      //-------   This is for passing in arguments to do auto processing.   -------
     errCodeList = dialog.processRunArguments();

     dialog.setLocationRelativeTo( null );
      
      if ( ! dialog.isAutoProcessedAFile() && ! argsHM.containsKey( "junitflag" ) )
          {
          dialog.setVisible( true );
          }
   }

    public ArrayList<Integer> getErrCodeList() {
        return errCodeList;
    }

   @Override
   public String getVendorURL()
   {
      return URL;
   }

   @Override
   public String getVendor()
   {
      return VENDOR;
   }

   @Override
   public Image getIconImage()
   {
      return image;
   }

   public FeatureModuleContext getMainContext()
   {
      return getContext();
   }
}
