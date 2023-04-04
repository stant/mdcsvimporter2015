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

// Fixed by Stuart Beesley - April 2023
//       Fixed filenames check when null in ImportDialog.java
//       Tweaked with some more up-to-date MD API calls
//       Fixed calls to StringUtils for MD2023...
//       Fixed calls to JFileChooser for Mac; also so that FileDialog gets called instead...
//       Fixed other NPEs found...
//       Fixed minimum screen width on main screen so that the ... file browser button is not hidden
//       Changed extension build version to 1000
//       Fixed another NPE on main dialog screen...

package com.moneydance.modules.features.mdcsvimporter;

import com.infinitekind.moneydance.model.AccountBook;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.view.gui.MoneydanceGUI;

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main extends FeatureModule
{

    public static boolean DEBUG = false;

    public static final int MIN_MD_BUILD = 1372;    // Runs on MD2015.8(1372) onwards...
    public static final String EXTN_ID = "mdcsvimporter2015";
    public static final String EXTN_NAME = "CSV Importer";

    public static com.moneydance.apps.md.controller.Main MD_REF;

//   private static final int VERSION = 23;
//   protected static final String VERSION_STRING = " " + VERSION + " for MD2022";
//   private static final String NAME = "CSV Importer";
//   private static final String VENDOR = "Stan Towianski";
//   private static final String URL = "https://github.com/stant/mdcsvimporter2015";
//   private static final String DESCRIPTION =
//      "Let's you create configs for say: Discover card, VISA, your private bank, etc... " +
//      "You denote columns like: -Payment-, -Deposit-, date, amount, memo, etc... " +
//      "It can test your file, giving you a list of all the readers that can handle your file. " +
//      "Importing does matching to skip duplicate entries."
//           ;

   //   private static Image image;

   private ArrayList<Integer> errCodeList = null;

//   {
//      try
//      {
//         image = ImageIO.read( Main.class.getResourceAsStream( "import.png" ) );
//      }
//      catch ( IOException x )
//      {
//         // ignore error; nothing we can do about it
//      }
//   }

   public Main()
   {
   }
   
   /*
    public static void main(String args[]) 
    {
       String amt = "($157.86)";
       Util.logTerminal( "converted amount =" + amt.replaceAll( "\\((.*)\\)", "-$1" ) );

       amt = "$123.86";
       Util.logTerminal( "converted amount =" + amt.replaceAll( "\\((.*)\\)", "-$1" ) );
   }
    */

    @Override
    public void init() {
        FeatureModuleContext context = getContext();
        if (context == null) {
            Util.logConsole("*** Error: got context == null");
            return;
        }

        MD_REF = (com.moneydance.apps.md.controller.Main) context;

        if (context.getBuild() < MIN_MD_BUILD) {
            Util.logConsole("ALERT: This extension/widget is only supported on MD2015.8(1372) onwards... Quitting.....");
            return;
        }

        context.registerFeature(this, "import", null, EXTN_NAME);
        Util.logConsole(EXTN_NAME + " >> Initialised... Build:" + getBuild());
    }

   AccountBook getAccountBook()
   {
      FeatureModuleContext context = getContext();
      return context.getCurrentAccountBook();
   }

    public static com.moneydance.apps.md.controller.Main getMDMain() {
        return MD_REF;
    }

    public static MoneydanceGUI getMDGUI() {
        return (MoneydanceGUI) getMDMain().getUI();
    }

   public FeatureModuleContext getMainContext()
   {
      return getContext();
   }

   JFrame getMoneydanceWindow() { return getMDGUI().getFirstMainFrame();}

//   JFrame getMoneydanceWindow()
//   {
//        return getMDGUI().getFirstMainFrame();
//
//      // Using undocumented feature. This way our windows and dialogs can have a parent,
//      // and behave more conformingly. Alternative is just returning null. Effects should
//      // be minor visual inconsistencies.
//
//      FeatureModuleContext context = getContext();
//      com.moneydance.apps.md.controller.Main main =
//         (com.moneydance.apps.md.controller.Main) context;
//      if ( main == null )
//      {
//         return null;
//      }
//      com.moneydance.apps.md.view.gui.MoneydanceGUI gui =
//         (com.moneydance.apps.md.view.gui.MoneydanceGUI) main.getUI();
//      if ( gui == null )
//      {
//         return null;
//      }
//      return gui.getTopLevelFrame();
//   }

   @Override
   public String getName()
   {
      return EXTN_NAME;
   }

//   @Override
//   public int getBuild()
//   {
//      return VERSION;
//   }

//   @Override
//   public String getDescription()
//   {
//      return DESCRIPTION;
//   }

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

       Util.logConsole("Received .invoke() command: '" + uri + "'");
      StringTokenizer tokenizer = new StringTokenizer( uri, "&" );
      HashMap argsHM = new HashMap();
      
      //filename="file"&fileformat="file format"&dateformat="date format"&importaccount="my account"
      //deletecsvfileflag&importtype="online|regular"
        
      //int count = tokenizer.countTokens();
      //String url = count + " tokens(";
//      Util.logConsole( "uri string =" + uri + "=" );

      while ( tokenizer.hasMoreTokens() )
          {
         //url = url.concat( tokenizer.nextToken() );
          String [] pcs = tokenizer.nextToken().split( "=" );
          Util.logConsole( "arg token [0] =" + pcs[0] + "=   token[1] =" + (pcs.length < 2 ?  "" : pcs[1]) + "=" );
          if ( pcs.length > 1 )
              {
              if ( pcs[1].startsWith( "\"" ) )
                  {
                  argsHM.put( pcs[0].toLowerCase(),  pcs[1].substring( 1, pcs[1].length() - 1 ) );
                  Util.logConsole("arg key =" + pcs[0].toLowerCase() + "=   value =" + pcs[1].substring( 1, pcs[1].length() - 1 ) + "=" );
                  }
              else
                  {
                  argsHM.put( pcs[0].toLowerCase(),  pcs[1] );
                  Util.logConsole("arg key =" + pcs[0].toLowerCase() + "=   value =" + pcs[1] + "=" );
                  }
              }
          else
              {
              argsHM.put( pcs[0].toLowerCase(),  null );
              Util.logConsole("arg key =" + pcs[0].toLowerCase() + "=   value =" + null + "=" );
              }
          }
      argsHM.remove( "import" );  // This seems to be passed in and I do not know why.
    
      ImportDialog dialog = new ImportDialog( this, argsHM );

      //-------   This is for passing in arguments to do auto processing.   -------
     errCodeList = dialog.processRunArguments();

     //dialog.setLocationRelativeTo( null );
      
      if ( ! dialog.isAutoProcessedAFile() && ! argsHM.containsKey( "junitflag" ) )
          {
          dialog.pack();
          WinProps winProps = Settings.getWinProps( true, "winprops.ImportDialog" );
          dialog.setLocation( winProps.getAtX(), winProps.getAtY() );
          dialog.setSize( new Dimension( winProps.getWidth(), winProps.getHeight() ) );
          //Util.logConsole( "winProps.getWidth() =" + winProps.getWidth() + "=" );
          //Util.logConsole( "winProps.getHeight() =" + winProps.getHeight() + "=" );

          dialog.setVisible( true );
          }
   }

    public ArrayList<Integer> getErrCodeList() {
        return errCodeList;
    }

//   @Override
//   public String getVendorURL()
//   {
//      return URL;
//   }

//   @Override
//   public String getVendor()
//   {
//      return VENDOR;
//   }

//   @Override
//   public Image getIconImage()
//   {
//      return image;
//   }

}
