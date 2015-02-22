/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
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

import com.moneydance.modules.features.mdcsvimporter.formats.CustomReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author miki and Stan Towianski
 */
public final class Settings
{    
    static HashMap<String, CustomReaderData> ReaderConfigsHM = null;
    static HashMap<String, TransactionReader> ReaderHM = null;
    static Properties currentProps = new Properties();
    static String emptyArrayProperty = "[,,,, , , , , , ]";
  //  static String emptyRegexsArrayProperty = "[\u001F \u001F \u001F \u001F \u001F \u001F \u001F \u001F \u001F  ]";
    //static String emptyRegexsArrayProperty = "[ a a a a a a a a a  ]";
   public static File getFilename()
   {
      System.err.println( "os.name =" + System.getProperty( "os.name" ) + "=" );
      File moneydanceHome = null;
      File moneydanceHome1 = null;
      File moneydanceHome2 = null;
      File moneydanceHome3 = null;
      File moneydanceHome4 = null;
      String missingHomeErrMsg = "";
              
      if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac" ) )
        {
        moneydanceHome1 = new File( System.getProperty( "user.home" ) + "/Library/Application Support", "Moneydance" );
        System.err.println( "try moneydanceHome folder =" + moneydanceHome1 + "=" );
        if ( moneydanceHome1.exists() )
            {
            moneydanceHome = moneydanceHome1;
            }
        else
            {
            moneydanceHome2 = new File( System.getProperty( "user.home" ) + "/Library/Preferences", "Moneydance" );
            System.err.println( "try moneydanceHome folder =" + moneydanceHome2 + "=" );
            if ( moneydanceHome2.exists() )
                {
                moneydanceHome = moneydanceHome2;
                }
            else
                {
                moneydanceHome3 = new File( "/Library/Preferences", "Moneydance" );
                System.err.println( "try moneydanceHome folder =" + moneydanceHome3 + "=" );
                if ( moneydanceHome3.exists() )
                    {
                    moneydanceHome = moneydanceHome3;
                    }
                else
                    {
                    moneydanceHome4 = new File( System.getProperty( "user.home" ) + "/Library", "Moneydance" );
                    System.err.println( "try moneydanceHome folder =" + moneydanceHome4 + "=" );
                    if ( moneydanceHome4.exists() )
                        moneydanceHome = moneydanceHome4;
                    } // 3
                } // 2
            } // 1
        
        // I am assuming at this point that these Mac folders do exist.
        
        if ( moneydanceHome == null )
            {
            System.err.println( "Could not find so assuming moneydanceHome folder =" + moneydanceHome1 + "=" );
            moneydanceHome = moneydanceHome1;
            missingHomeErrMsg = "\n\nI looked in these 4 places in this order: \n\n"
                        + moneydanceHome1 + "\n"
                        + moneydanceHome2 + "\n"
                        + moneydanceHome3 + "\n"
                        + moneydanceHome4 + "\n";
            }
        }
      else  // windows + Linux : test for moneydance folder
        {
        moneydanceHome1 = new File( System.getProperty( "user.home" ), ".moneydance" );
        System.err.println( "try moneydanceHome folder =" + moneydanceHome1 + "=" );
        if ( moneydanceHome1.exists() )
            moneydanceHome = moneydanceHome1;

        if ( moneydanceHome == null )
            {
            System.err.println( "Could not find so assuming moneydanceHome folder =" + moneydanceHome1 + "=" );
            moneydanceHome = moneydanceHome1;
            missingHomeErrMsg = "";   //\n\nI looked in this place: \n\n"
                                      //+ moneydanceHome + "\n";
            }
        }

      // for all os's
    if ( ! moneydanceHome.exists() )
        {
        boolean ok = moneydanceHome.mkdirs();
        JOptionPane.showMessageDialog( null, "Importer could not find a Moneydance Home directory so I created one here: \n\n" + moneydanceHome
                                        + missingHomeErrMsg
                                        );
        if ( ! ok )
            {
            JOptionPane.showMessageDialog( null, "*** Error creating Moneydance Home directory: \n\n" + moneydanceHome );
            }
        }
    moneydanceHome = new File( moneydanceHome, "mdcsvimporter.props" );
      
      // all systems - moneydanceHome now includes properties file path
      try {
        if ( ! moneydanceHome.exists() )
            {
            moneydanceHome.createNewFile();
            JOptionPane.showMessageDialog( null, "Importer could not find its properties files so I created one here: \n\n" + moneydanceHome
                        );
            }
        }
      catch (IOException ex) 
        {
        Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }

      return moneydanceHome;
   }

   private static Properties load()
      throws IOException
   {
      currentProps = new Properties();
      
      InputStream is;
      try
      {
         is = new FileInputStream( getFilename() );
      }
      catch ( FileNotFoundException ex )
      {
         return currentProps; // no file is normal condition to start with empty props object
      }
      try
      {
         currentProps.load( is );
      }
      finally
      {
         is.close();
      }
      return currentProps;
   }

   private static void save( Properties props )
      throws IOException
   {
      OutputStream os = new FileOutputStream( getFilename() );  //, Charset.forName( "UTF-8" ) ); //(String) transReader.getCustomReaderData().getFileEncoding() ) );
      try
      {
         props.store( os, "MDCSVImporter - Moneydance CSV Importer" );
      }
      finally
      {
         os.close();
         load();
      }
   }

   public static String get( boolean loadProps, String name )
   {
      try
      {
         if ( loadProps )
            {
            load();
            }
         return currentProps.getProperty( name );
      }
      catch ( IOException ex )
      {
         Logger.getLogger( Settings.class.getName() ).log( Level.SEVERE, null, ex );
         return null;
      }
   }
           
   public static String get( boolean loadProps, String name, String defaultValue )
    {
    String retVal = get( loadProps, name );
    if ( retVal == null )
        {
        return defaultValue;
        }
    return retVal;
    }

   public static void set( String name, String value )
   {
      try
      {
         Properties props = load();

        setOnly( props, name, value );

        save( props );
      }
      catch ( IOException ex )
      {
         Logger.getLogger( Settings.class.getName() ).log( Level.SEVERE, null, ex );
      }
   }

   public static void setOnly( Properties props, String name, String value )
   {
         // skip if values match (I am sorry for not optimizing the condition, it is early morning...)
         String oldValue = props.getProperty( name );
         if ( (oldValue != null && oldValue.equals( value )) ||
            (value != null && value.equals( oldValue )) )
         {
            return;
         }

         props.setProperty( name, value );
   }

   public static boolean getBoolean( boolean loadProps, String name )
   {
      return getBoolean( loadProps, name, false );
   }

   public static boolean getBoolean( boolean loadProps, String name, boolean defaultValue )
   {
      String value = get( loadProps, name );
      if ( value == null )
      {
         return defaultValue;
      }

      if ( value.equalsIgnoreCase( "true" ) || value.equalsIgnoreCase( "yes" ) ||
         value.equalsIgnoreCase( "1" ) )
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   public static void setBoolean( String name, boolean value )
   {
      set( name, value ? "true" : "false" );
   }

   public static void setYesNo( String name, boolean value )
   {
      set( name, value ? "yes" : "no" );
   }

   public static int getInteger( boolean loadProps, String name )
   {
      return getInteger( loadProps, name, 0 );
   }

   public static int getInteger( boolean loadProps, String name, int defaultValue )
   {
      String value = get( loadProps, name );
      if ( value == null )
      {
         return defaultValue;
      }

      return Integer.parseInt( value );
   }

   public static void setInteger( String name, int value )
   {
      set( name, Integer.toString( value ) );
   }

   public static HashMap<String, CustomReaderData> createReaderConfigsHM()
   {
      ReaderConfigsHM = new HashMap<String, CustomReaderData>();
      ReaderHM = new HashMap<String, TransactionReader>();

      try
      {
        Properties props = load();

        for ( Enumeration enu = props.propertyNames(); enu.hasMoreElements(); )
            {
            String key = (String) enu.nextElement();
            System.out.println( "props key =" + key + "=" );
            if ( key.startsWith( "reader:" ) && key.endsWith( ".Name" ) )
                {
                String readerName = key.replaceAll( "reader\\:(.*)\\..*", "reader:$1" );
                System.err.println(  "readerName >" + readerName + "<" );
                   
                CustomReaderData customReaderData = new CustomReaderData();
                customReaderData.setReaderName( props.getProperty( readerName + ".Name" ) );
                customReaderData.setFieldSeparatorChar( getInteger( false, readerName + ".FieldSeparator", ',' ) );
                customReaderData.setDateFormatString( props.getProperty( readerName + ".DateFormatString" ) );
                customReaderData.setFileEncoding( props.getProperty( readerName + ".FileEncodingString" ) );

                customReaderData.setHeaderLines( getInteger( false, readerName + ".HeaderLines", 0 ) );
                customReaderData.setFooterLines( getInteger( false, readerName + ".FooterLines", 0 ) );

                customReaderData.setAmountCurrencyChar( getInteger( false, readerName + ".AmountCurrencyChar", '$' ) );
                customReaderData.setAmountDecimalSignChar( getInteger( false, readerName + ".AmountDecimalSignChar", '.' ) );
                customReaderData.setAmountGroupingSeparatorChar( getInteger( false, readerName + ".AmountGroupingSeparatorChar", ',' ) );
                customReaderData.setAmountFormat( props.getProperty( readerName + ".AmountFormat" ) );
                customReaderData.setImportReverseOrderFlg( getBoolean( false, readerName + ".ImportReverseOrderFlag", false ) );
                customReaderData.setUseRegexFlag(getBoolean( false, readerName + ".UseRegexFlag", false ) );
                customReaderData.setFilenameMatcher(props.getProperty( readerName + ".FilenameMatcher" ) );
                
                //customReaderData.setRegexsList( new ArrayList<String>(Arrays.asList( props.getProperty( readerName + ".RegexsList", emptyRegexsArrayProperty ).split( "[\\[\\]a]" ) ) ) );
                //customReaderData.setRegexsList( new ArrayList<String>( 10 ) );
                customReaderData.setRegexsList( new ArrayList<String>(Arrays.asList( "", "", "", "", "", "", "", "", "", "" ) ) );
                customReaderData.setDataTypesList( new ArrayList<String>(Arrays.asList( props.getProperty( readerName + ".DataTypesList", emptyArrayProperty ).split( "[\\[\\],]" ) ) ) );
                customReaderData.setEmptyFlagsList( new ArrayList<String>(Arrays.asList( props.getProperty( readerName + ".EmptyFlagsList", emptyArrayProperty ).split( "[\\[\\],]" ) ) ) );

                int max = customReaderData.getDataTypesList().size();
                System.err.println( "props customReaderData.getRegexsList().size() =" + customReaderData.getRegexsList().size() + "=   max =" + max );
                for ( int c = 1; c < max; c++ )
                    {
                    customReaderData.getRegexsList().set( c - 1, props.getProperty( readerName + ".RegexsList." + (c-1), "" ) );
                    //customReaderData.getRegexsList().set( c - 1,customReaderData.getRegexsList().get( c ).trim() );
                    customReaderData.getDataTypesList().set( c - 1,customReaderData.getDataTypesList().get( c ).trim() );
                    customReaderData.getEmptyFlagsList().set( c - 1,customReaderData.getEmptyFlagsList().get( c ).trim() );
                    }

                /*
                if ( props.getProperty( readerName + ".DateFormatList" ) != null )
                    {
                    customReaderData.setDateFormatList( new ArrayList<String>(Arrays.asList( props.getProperty( readerName + ".DateFormatList" ).split( "[\\[\\],]" ) ) ) );
                    }
                else
                    {
                    customReaderData.setDateFormatList( new ArrayList<String>() );
                    }
                max = customReaderData.getDateFormatList().size();
                for ( int c = 1; c < max; c++ )
                    {
                    customReaderData.getDateFormatList().set( c - 1,customReaderData.getDateFormatList().get( c ).trim() );
                    }
                 */
                System.err.println( "props readerName =" + customReaderData.getReaderName() + "=" );
                System.err.println( "props getFieldSeparatorChar() =" + customReaderData.getFieldSeparatorChar() + "=" );
                System.err.println( "props getFileEncoding() =" + customReaderData.getFileEncoding() + "=" );
                System.err.println( "props getDateFormatString() =" + customReaderData.getDateFormatString()+ "=" );
                System.err.println( "props getHeaderLines() =" + customReaderData.getHeaderLines() + "=" );
                System.err.println( "props getRegexsList() =" + customReaderData.getRegexsList() + "=" );
                System.err.println( "props getDataTypesList() =" + customReaderData.getDataTypesList() + "=" );
                System.err.println( "props getEmptyFlagsList() =" + customReaderData.getEmptyFlagsList() + "=" );
                
                ReaderConfigsHM.put( props.getProperty( readerName + ".Name" ), customReaderData );
                
                CustomReader customReader = new CustomReader( customReaderData );
                ReaderHM.put( props.getProperty( readerName + ".Name" ), customReader );
                
                customReader.createSupportedDateFormats( customReaderData.getDateFormatString() );                
                }
            }
          }
      catch ( IOException ex )
         {
         Logger.getLogger( Settings.class.getName() ).log( Level.SEVERE, null, ex );
         return null;
         }
      
      return ReaderConfigsHM;
   }

    public static HashMap<String, TransactionReader> getReaderHM() {
        return ReaderHM;
    }

   public static void setCustomReaderConfig( CustomReaderData customReaderData )
   {
      try
      {
         Properties props = load();

         setOnly( props, "reader:" + customReaderData.getReaderName() + ".Name", customReaderData.getReaderName() );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".HeaderLines", Integer.toString( customReaderData.getHeaderLines() ) );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".FooterLines", Integer.toString( customReaderData.getFooterLines() ) );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".FieldSeparator", Integer.toString( customReaderData.getFieldSeparatorChar() ) );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".FileEncodingString", customReaderData.getFileEncoding() );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".DateFormatString", customReaderData.getDateFormatString() );
         //setOnly( props, "reader:" + customReaderData.getReaderName() + ".RegexsList", customReaderData.getRegexsListEncoded() );
         for( int c = 0; c < 10; c++ )
            {
            setOnly( props, "reader:" + customReaderData.getReaderName() + ".RegexsList." + c, customReaderData.getRegexsListEle( c ) );
            }
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".DataTypesList", customReaderData.getDataTypesList().toString() );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".EmptyFlagsList", customReaderData.getEmptyFlagsList().toString() );
         //setOnly( props, "reader:" + customReaderData.getReaderName() + ".DateFormatList", customReaderData.getDateFormatList().toString() );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".AmountCurrencyChar", Integer.toString( customReaderData.getAmountCurrencyChar() ) );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".AmountDecimalSignChar", Integer.toString( customReaderData.getAmountDecimalSignChar() ) );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".AmountGroupingSeparatorChar", Integer.toString( customReaderData.getAmountGroupingSeparatorChar() ) );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".AmountFormat", customReaderData.getAmountFormat() );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".ImportReverseOrderFlag", Boolean.toString( customReaderData.getImportReverseOrderFlg() ) );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".UseRegexFlag", Boolean.toString( customReaderData.getUseRegexFlag() ) );
         setOnly( props, "reader:" + customReaderData.getReaderName() + ".FilenameMatcher", customReaderData.getFilenameMatcher() );

         save( props );
      }
      catch ( IOException ex )
      {
         Logger.getLogger( Settings.class.getName() ).log( Level.SEVERE, null, ex );
      }
   }

   public static void removeCustomReaderConfig( CustomReaderData customReaderData )
   {
      try
      {
         Properties props = load();
         
         props.remove( "reader:" + customReaderData.getReaderName() + ".Name" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".HeaderLines" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".FooterLines" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".FieldSeparator" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".DateFormatString" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".RegexsList" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".DataTypesList" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".EmptyFlagsList" );
         //props.remove( "reader:" + customReaderData.getReaderName() + ".DateFormatList" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".FooterLines" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".AmountCurrencyChar" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".AmountDecimalSignChar" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".AmountGroupingSeparatorChar" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".AmountFormat" );
         props.remove( "reader:" + customReaderData.getReaderName() + ".ImportReverseOrderFlag" );

         save( props );
      }
      catch ( IOException ex )
      {
         Logger.getLogger( Settings.class.getName() ).log( Level.SEVERE, null, ex );
      }
   }
}
