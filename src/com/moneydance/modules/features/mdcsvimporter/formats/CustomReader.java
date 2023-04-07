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
package com.moneydance.modules.features.mdcsvimporter.formats;

import com.infinitekind.moneydance.model.OnlineTxn;
import com.moneydance.modules.features.mdcsvimporter.CSVData;
import com.moneydance.modules.features.mdcsvimporter.CustomReaderData;
import com.moneydance.modules.features.mdcsvimporter.TransactionReader;
import com.moneydance.modules.features.mdcsvimporter.Util;
import com.infinitekind.util.CustomDateFormat;
import com.infinitekind.util.StringUtils;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Stan Towianski     August 2011
 */
public class CustomReader extends TransactionReader
{
    public static final String DATA_TYPE_BLANK = "";
    public static final String DATA_TYPE_IGNORE = "ignore";
    public static final String DATA_TYPE_IGNORE_REST = "ignore rest";
    public static final String DATA_TYPE_PAYMENT = "-Payment-";
    public static final String DATA_TYPE_DEPOSIT = "-Deposit-";
    public static final String DATA_TYPE_DATE = "date";
    public static final String DATA_TYPE_DATE_AVAILABLE = "date available";
    public static final String DATA_TYPE_DATE_INITIATED = "date initiated";
    public static final String DATA_TYPE_DATE_POSTED = "date posted";
    public static final String DATA_TYPE_DATE_PURCHASED = "date purchased";
    public static final String DATA_TYPE_CHECK_NUMBER = "check number";
    public static final String DATA_TYPE_DESCRIPTION = "description";
    public static final String DATA_TYPE_MEMO = "memo";
    public static final String DATA_TYPE_ACCOUNT_NAME = "account name";
    public static final String DATA_TYPE_CATEGORY_NAME = "category name";

    private static final String DATE_FORMAT_US = "MM/DD/YYYY";
    private static final String DATE_FORMAT_EU = "DD/MM/YY";
    private static final String DATE_FORMAT_JP = "YY/MM/DD";
    private static final String DATE_FORMAT_INTN = "YYYY-MM-DD";

    private String dateFormatStringSelected = DATE_FORMAT_US;
   
   private static String[] SUPPORTED_DATE_FORMATS =
   {
      DATE_FORMAT_US
           , DATE_FORMAT_EU
           , DATE_FORMAT_JP
           , DATE_FORMAT_INTN
   };

    private CustomDateFormat dateFormat = new CustomDateFormat( DATE_FORMAT_US );
    //private CustomDateFormat dateFormat;
   
    //private String[] compatibleDateFormats;
    private String dateFormatString;

    private long amount = 0;
    private int date = 0;
    private int dateAvailable = 0;
    private int dateInitiated = 0;
    private int datePosted = 0;
    private int datePurchased = 0;
    private String description = "";
    private String checkNumber = "";
    private String phoneString;
    private String memo;
    private String accountName;
    private String categoryName;
   
   public CustomReader( CustomReaderData customReaderData )
        {
        setCustomReaderData( customReaderData );
        setCustomReaderFlag( true );
        }
   
   
   @Override
    public void setSupportedDateFormats( String[] supportedDateFormats ) 
        {
        SUPPORTED_DATE_FORMATS = supportedDateFormats;
        }
    
    public void createSupportedDateFormats( String dateFormatArg ) 
        {
         Util.logConsole(  "\n---------   entered createSupportedDateFormats() dateFormatArg =" + dateFormatArg + "=  -------------" );
        String[] tmp = new String[1];
        tmp[0] = dateFormatArg;
        SUPPORTED_DATE_FORMATS = tmp;
        setDateFormat( dateFormatArg );
        }
	
   @Override
   public boolean canParse( CSVData data, int parseThruErrors )
        {
        Util.logConsole(  "---------   entered customerReader().canParse() as type =" + getFormatName() + "=  -------------" );
        try {
            data.parseIntoLines( getCustomReaderData() );
            } 
        catch (IOException ex) 
            {
            //Logger.getLogger(CustomReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
            }

      //if ( data.getReader() == null )
       //   Util.logConsole( "data.getReader() == null" );
          
      //Util.logConsole( "at parse getFieldSeparator() =" + (char)csvData.getReader().getFieldSeparator() + "=" );
      //csvData.getReader().setFieldSeparator( getCustomReaderData().getFieldSeparatorChar() );
      //Util.logConsole( "at parse getFieldSeparator() after set =" + (char)csvData.getReader().getFieldSeparator() + "=" );
        
        data.reset();
        long fileLineCount = 0;
        long endingBlankLines = 0;
        //----- Count File Lines to know where Footer starts  -----
        while ( data.nextLine() )
            {
            fileLineCount ++;
            if ( ! data.hasZeroFields() )
                {
                endingBlankLines ++;
                Util.logConsole(  "endingBlankLines =" + endingBlankLines );
                }
            else
                {
                endingBlankLines = 0;
                }
            }
        Util.logConsole(  "fileLineCount =" + fileLineCount );

        data.reset();
        int skipHeaderLines = getHeaderCount();
        Util.logConsole(  "skip any Header Lines =" + skipHeaderLines );
        for ( int i = 0; i < skipHeaderLines; i++ )
            {
            Util.logConsole( "skip header line" );
            data.nextLine();
            }
      long begAtLine = data.getCurrentLineIndex() + 1;
      
      boolean retVal = true;
      int maxFieldIndex = getCustomReaderData().getNumberOfCustomReaderFieldsUsed();

      setDateFormat( getCustomReaderData().getDateFormatString() );
      
      // convert to validate with Java date formatting d,y, and M. case matters.
      String jDateFormat = getCustomReaderData().getDateFormatString().toLowerCase();
      jDateFormat = jDateFormat.replace( 'm', 'M' );
      SimpleDateFormat sdf = new SimpleDateFormat( jDateFormat, Locale.ENGLISH );
      sdf.setLenient( false );
      
      Util.logConsole(  "using dateFormat string =" + getCustomReaderData().getDateFormatString() + "->" + jDateFormat + "<-" );
      
      long totalProcessed = 0;
      long stopAtLine = fileLineCount - getHeaderCount() - getCustomReaderData().getFooterLines() - endingBlankLines;
//		priorAccountNameFromCSV = "";
//		Util.logTerminal("calling while (csvData.nextLine())...");
 
//    data.printFile();
//    data.reverseListRangeOrder( begAtLine, stopAtLine - 1 );
//    data.printFile();
    
      
      while ( ( retVal || (parseThruErrors == TransactionReader.PARSE_THRU_ERRORS_CONTINUE) )
                && data.nextLine() && totalProcessed < stopAtLine )
         {
         totalProcessed++;
         Util.logConsole(  "------- next line ---------------" );
         if ( ! data.hasZeroFields() )
            {
            continue; // skip empty lines
            }

         if ( ! data.hasEnoughFieldsPerCurrentLine( maxFieldIndex - 1 ) )
            {
            Util.logConsole( "Have too few fields. Needed >= " + ( maxFieldIndex - 1 ) );
            data.printCurrentLine();
            retVal = false;
            data.nextField();  // read to first field since we did not start yet.
            data.setFieldErr( "Have too few fields. Needed >= " + ( maxFieldIndex - 1 ) );
            continue;
            }

         int fieldIndex = 0;
         Util.logConsole(  "maxFieldIndex =" + maxFieldIndex );
         
         for (           ; ( retVal  || (parseThruErrors == TransactionReader.PARSE_THRU_ERRORS_CONTINUE) )
                 && fieldIndex < maxFieldIndex; fieldIndex ++ )
             {
             String dataTypeExpecting = getCustomReaderData().getDataTypesList().get( fieldIndex );
             //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  fieldIndex = " + fieldIndex );

             data.nextField();
//             if ( ! data.nextField() )
//                {
//                //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but have no data left." );
//                retVal = false;
//                break;
//                }
             String fieldString = data.getField();
             
             if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_IGNORE_REST ) )
                {
                while ( fieldIndex < maxFieldIndex )
                    {
                    fieldIndex ++;
                    data.nextField();
                    fieldString += data.getField();
                    }
                break;
                }
             else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_IGNORE ) )
                {
                int x = 1;
                try
                    {
                    x = Integer.parseInt( getCustomReaderData().getEmptyFlagsList().get( fieldIndex ).trim() );
                    Util.logConsole(  "ignore " + x + " lines" );
                    }
                catch ( Exception ex )
                    {
                    Util.logConsole(  "assume ignore 1 line by erro on field =" + getCustomReaderData().getEmptyFlagsList().get( fieldIndex ).trim() + "=" );
                    }
                while ( x > 1 )
                    {
                    data.nextField();
                    fieldString = data.getField();
                    Util.logConsole(  "ignore fieldString =" + fieldString + "=  fieldIndex = " + fieldIndex );
                    //fieldIndex ++;  NO - just skip data not field data type index
                    x--;
                    }
                continue;
                }
             else if ( ( fieldString == null || fieldString.equals( DATA_TYPE_BLANK ) ) )
                {
                if ( ! getCustomReaderData().getEmptyFlagsList().get( fieldIndex ).equals( "Can Be Blank" ) )
                    {
                    //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but got no value =" + fieldString + "= and STOP ON ERROR" );
                    retVal = false;
                    data.setFieldErr( "Field Cannot Be Blank" );
                    //break;
                    }
                else
                    {
                    Util.logConsole(  "ok to skip this blank field" );
                    continue;
                    }
                }
                
             if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE ) 
                   ||  dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE_AVAILABLE )
                   ||  dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE_INITIATED )
                   ||  dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE_POSTED )
                   ||  dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE_PURCHASED )
                     )
                {
                Util.logConsole(  "date >" + fieldString + "<" );
                
                /*
                  // find guessable date formats
                //  if ( retVal )
                  {
                  DateGuesser guesser = new DateGuesser();
                  guesser.checkDateString( fieldString );
 
                     //compatibleDateFormats = guesser.getPossibleFormats();
                     SUPPORTED_DATE_FORMATS = guesser.getPossibleFormats();
                     importDialog.popComboDateFormatList( SUPPORTED_DATE_FORMATS );
                     if ( dateFormatStringSelected == null ||
                        ! findDateFormat( SUPPORTED_DATE_FORMATS, dateFormatStringSelected ) )
                     {
                        setDateFormat( guesser.getBestFormat() );
                     }
                  }
                */
              /**/
//                 if ( dateFormat.parseInt( fieldString ) != dateFormat.parseInt( dateFormat.format( dateFormat.parseInt( fieldString ) ) ) )
//                 {
//                    retVal = false;
//                    break;
//                 }
                Util.logConsole(  "fieldString =" + fieldString + "=   date formatted >" + dateFormat.format( dateFormat.parseInt( fieldString ) ) + "<" );
      
                try {
                    sdf.parse( fieldString );  // This seems to catch jan 32 -> feb 01 which I do not want to allow.

                    // won't work when fieldString is 3/5/2012 because it will compare incorrectly to created 03/05/2012 and don't know how to fix that! !
//                    if ( ! sdf.fieldString.equals( dateFormat.format( dateFormat.parseInt( fieldString ) ) ) )
//                        {
//                        retVal = false;
//                        break;
//                        }
                }
                catch (ParseException e) {
                    Util.logConsole(  "canParse() parseException: " + sdf.toString() + "<" );
                    retVal = false;
                    data.setFieldErr( "Invalid Date" );
                    //break;
                }
                catch (IllegalArgumentException e) {
                    Util.logConsole(  "canParse() IllegalArgumentException: " + sdf.toString() + "<" );
                    retVal = false;
                    data.setFieldErr( "Invalid Date" );
                    //break;
                }

                /**/
                }
             else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_PAYMENT ) 
                         || dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DEPOSIT ) )   // was only amount before
                {
                Util.logConsole(  "amountString >" + fieldString + "<" );
                fieldString = fieldString.replaceAll( "\\((.*)\\)", "-$1" );
                Util.logConsole(  "amountString >" + fieldString + "<" );
                fieldString = fieldString.replaceAll( "[^0-9]*(.*)", "$1" ); // strip leading non-digits
                Util.logConsole(  "amountString >" + fieldString + "<" );

                try
                     {
                        //StringUtils.parseDoubleWithException( fieldString, '.' );
                        String tmp = fieldString.replace( '$', '0' );
                        //Util.logConsole(  "check modified amountString 1 >" + tmp + "<" );
                        tmp = tmp.replace( '-', '0' );
                        //Util.logConsole(  "check modified amountString 2 >" + tmp + "<" );
                        tmp = tmp.replaceAll( " ", "" );
                        //Util.logConsole(  "check modified amountString 3 >" + tmp + "<" );
                        tmp = tmp.replaceAll( ",", "" );
                        //Util.logConsole(  "check modified amountString 4 >" + tmp + "<" );
                        tmp = tmp.replaceAll( "\\.", "" );
                        //Util.logConsole(  "check modified amountString 5 >" + tmp + "<" );
                        tmp = tmp.replaceAll( "\\d", "" );
                        Util.logConsole(  "check modified amountString 6 >" + tmp + "<" );
                        //Number number = NumberFormat.getNumberInstance().parse( tmp );
                        if ( tmp.equals( "" ) ) //number instanceof Double || number instanceof Long )
                            {
                            Util.logConsole(  "ok number" );
                            ;
                            }
                        else
                            {
                            retVal = false;
                            data.setFieldErr( "Invalid Number" );
                            //break;
                            }
                     }
                     catch ( Exception x )
                     {
                        x.printStackTrace();
                        retVal = false;
                        data.setFieldErr( "Invalid Number" );
                        //break;
                     }
                }
             else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DESCRIPTION ) )
                {
                Util.logConsole(  "description >" + fieldString + "<" );
                }
             else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_MEMO) )
                {
                Util.logConsole(  "memo >" + fieldString + "<" );
                }
             else if ( dataTypeExpecting.equalsIgnoreCase( "tag" ) )
                {
                Util.logConsole(  "tag >" + fieldString + "<" );
                }
             else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_ACCOUNT_NAME ) )
                {
                Util.logConsole(  "accountName >" + fieldString + "<" );
                accountName = fieldString;

                if ( book.getRootAccount().getAccountByName( accountName ) == null )
                    {
                    //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= will not import it." );
//                    //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= and STOP ON ERROR" );
//                    retVal = false;
//                    break;
                    }
                this.accountNameFromCSV = accountName;
                }
             else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_CATEGORY_NAME ) )
                {
                Util.logConsole(  "categoryName >" + fieldString + "<" );
                categoryName = fieldString;
                setUsingCategorynameFlag( true );
                
//                if ( rootAccount.getAccountByName( categoryName ) == null )
//                    {
//                    Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= will not import it." );
////                    Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= and STOP ON ERROR" );
////                    retVal = false;
////                    break;
//                    }
                //this.categoryNameFromCSV = categoryName;
                }
             } // end for
      }

      Util.logConsole( "canParse will return =" + retVal );
      return retVal;
   }

   @Override
   public String getFormatName()
   {
      return getCustomReaderData().getReaderName();
   }

   /*
    * Note: This really parses a whole line at a time.
    */
   @Override
   protected boolean parseNext() throws IOException
   {
     amount = 0;
     date = 0;
     dateAvailable = 0;
     dateInitiated = 0;
     datePosted = 0;
     datePurchased = 0;
     description = "";
     checkNumber = "";
     phoneString = "";
     memo = "";
     accountName = "";

     int fieldIndex = 0;
     int amountDecimalSignChar = getCustomReaderData().getAmountDecimalSignChar();
     int maxFieldIndex = getCustomReaderData().getNumberOfCustomReaderFieldsUsed();
     Util.logConsole(  "maxFieldIndex =" + maxFieldIndex );

     setDateFormat( getCustomReaderData().getDateFormatString() );
     Util.logConsole(  "using dateFormat string =" + getCustomReaderData().getDateFormatString() + "=" );
     
     Util.logConsole(  "----------------------" );
     if ( ! csvData.hasZeroFields() )
        {
        Util.logConsole(  "skip empty line" );
        return false; // skip empty lines
        }

     for (           ; fieldIndex < maxFieldIndex; fieldIndex ++ )
         {
         String dataTypeExpecting = getCustomReaderData().getDataTypesList().get( fieldIndex );
         //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  EmptyFlagsList = " + getCustomReaderData().getEmptyFlagsList().get( fieldIndex ) + "=" );

         csvData.nextField();
         String fieldString = csvData.getField();
         Util.logConsole(  "fieldString =" + fieldString + "=  fieldIndex = " + fieldIndex );

         if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_IGNORE_REST ) )
            {
            break;
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_IGNORE ) )
            {
            int x = 1;
            try
                {
                x = Integer.parseInt( getCustomReaderData().getEmptyFlagsList().get( fieldIndex ).trim() );
                Util.logConsole(  "ignore " + x + " lines" );
                }
            catch ( Exception ex )
                {
                Util.logConsole(  "ignore 1 line by erro on field =" + getCustomReaderData().getEmptyFlagsList().get( fieldIndex ).trim() + "=" );
                }
            while ( x > 1 )
                {
                csvData.nextField();
                fieldString = csvData.getField();
                Util.logConsole(  "ignore fieldString =" + fieldString + "=  fieldIndex = " + fieldIndex );
                //fieldIndex ++;  NO - just skip data not field data type index
                x--;
                }
            continue;
            }
         else if ( ( fieldString == null || fieldString.equals( "" ) )
                    && ! getCustomReaderData().getEmptyFlagsList().get( fieldIndex )
					.equals( "Can Be Blank" ) )
            {
            //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but got no value =" + fieldString + "= and STOP ON ERROR" );
            throwException( "dataTypeExpecting =" + dataTypeExpecting + "=  but got no value =" + fieldString + "= and STOP ON ERROR" );
            }
         
         if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE ) )
            {
            Util.logConsole( "date >" + fieldString + "<" );

            fieldString = convertMmmFormattedDate( fieldString, getCustomReaderData().getDateFormatString() );
            Util.logConsole( "MMM date str =" + fieldString + "=   date int  =" + dateFormat.parseInt( fieldString ) + "=   old date formatted >" + dateFormat.format( dateFormat.parseInt( fieldString ) ) + "<" );

            date = dateFormat.parseInt( fieldString );
            // I thought the format was giving incorrect dates for 2/5/2011 so I started doing my own thing. I later
            // found out the method I am calling uses an MD method which is working, and my new stuff was not so I left it out.  Stan
//            Date gotDate = parseDateToInt( fieldString, getCustomReaderData().getDateFormatString() );  // part of my new stuff not being used.
//            date = getIntDate( gotDate );
//            Util.logConsole(  "new date int =" + getIntDate( gotDate ) + "=   new date formatted >" + giveFormattedDate( gotDate, getCustomReaderData().getDateFormatString() ) + "<" );
            
//            txn.setDatePostedInt( date );
//            txn.setDateInitiatedInt( date );
//            txn.setDateAvailableInt( date );
          /*
             if ( !date.equals( dateFormat.format( dateFormat.parseInt( csvData.getField() ) ) ) )
             {
                retVal = false;
                break;
             }
          */
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE_AVAILABLE ) )
            {
            Util.logConsole(  "dateAvailable >" + fieldString + "<" );
            fieldString = convertMmmFormattedDate( fieldString, getCustomReaderData().getDateFormatString() );
            Util.logConsole( "MMM date str =" + fieldString + "=   date int  =" + dateFormat.parseInt( fieldString ) + "=   old date formatted >" + dateFormat.format( dateFormat.parseInt( fieldString ) ) + "<" );

            dateAvailable = dateFormat.parseInt( fieldString );
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE_INITIATED) )
            {
            Util.logConsole(  "dateInitiated >" + fieldString + "<" );
            fieldString = convertMmmFormattedDate( fieldString, getCustomReaderData().getDateFormatString() );
            Util.logConsole( "MMM date str =" + fieldString + "=   date int  =" + dateFormat.parseInt( fieldString ) + "=   old date formatted >" + dateFormat.format( dateFormat.parseInt( fieldString ) ) + "<" );

            dateInitiated = dateFormat.parseInt( fieldString );
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE_POSTED ) )
            {
            Util.logConsole(  "datePosted >" + fieldString + "<" );
            fieldString = convertMmmFormattedDate( fieldString, getCustomReaderData().getDateFormatString() );
            Util.logConsole( "MMM date str =" + fieldString + "=   date int  =" + dateFormat.parseInt( fieldString ) + "=   old date formatted >" + dateFormat.format( dateFormat.parseInt( fieldString ) ) + "<" );

            datePosted = dateFormat.parseInt( fieldString );
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DATE_PURCHASED ) )
            {
            Util.logConsole(  "datePurchased >" + fieldString + "<" );
            fieldString = convertMmmFormattedDate( fieldString, getCustomReaderData().getDateFormatString() );
            Util.logConsole( "MMM date str =" + fieldString + "=   date int  =" + dateFormat.parseInt( fieldString ) + "=   old date formatted >" + dateFormat.format( dateFormat.parseInt( fieldString ) ) + "<" );

            datePurchased = dateFormat.parseInt( fieldString );
            }
         else if ( ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_PAYMENT )
                      || dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DEPOSIT ) )
                                        &&
                     ! ( fieldString == null || fieldString.equals( "" ) ) )
            {
            Util.logConsole(  "amountString >" + fieldString + "<" );
            fieldString = fieldString.replaceAll( "\\((.*)\\)", "-$1" );
            fieldString = StringUtils.stripNonNumbers( fieldString, (char)amountDecimalSignChar );
            Util.logConsole(  "amountString >" + fieldString + "<" );
            
            try
                {
                double amountDouble = StringUtils.parseDoubleWithException( fieldString, (char)amountDecimalSignChar );
                if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_PAYMENT ) )
                    {
                    amount += currency.getLongValue( amountDouble );
                    }
                else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DEPOSIT ) )
                    {
                    Util.logConsole(  "flip sign for deposit" );
                    amount -= currency.getLongValue( amountDouble );
                    }
                }
            catch ( Exception x )
                {
                throwException( "Invalid amount." );
                }
//            txn.setAmount( amount );
//            txn.setTotalAmount( amount );
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_CHECK_NUMBER ) )
            {
            //origCheckNumber = fieldString;
            /*  changed matching to use original check number which contained leading 0's so go back to using that. Stan
            if ( fieldString != null )
                {
                    // NOTE: I had to do this because I could set ck # = 004567 but get() returns 4567 so matching would not work. Stan
                fieldString = fieldString.replaceAll( "^0*(.*)", "$1" );
                }
                 */
            Util.logConsole(  "check number >" + fieldString + "<" );
			checkNumber = fieldString;
//            txn.setCheckNum( fieldString );
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_DESCRIPTION ) )
            {
            Util.logConsole(  "description >" + fieldString + "<" );
//            txn.setName( fieldString );
            description = fieldString;
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_MEMO ) )
            {
            Util.logConsole(  "memo >" + fieldString + "<" );
			memo = fieldString;
//            txn.setMemo( fieldString );
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( "tag" ) )
            {
            Util.logConsole(  "tag in phone field >" + fieldString + "<" );
            // storing it into phone field for now since onlinetxn cannot handle tags. A kludge for now.  Stan
//            txn.setPhone( fieldString );
            phoneString = fieldString;
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_ACCOUNT_NAME ) )
            {
            Util.logConsole(  "accountName >" + fieldString + "<" );
            accountName = fieldString;

            if ( book.getRootAccount().getAccountByName( accountName ) == null )
                {
                //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= will not import it." );
//                //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= and STOP ON ERROR" );
//                throwException( "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= and STOP ON ERROR" );
                return false; // skip this line
                }
            this.accountNameFromCSV = accountName;
            }
         else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_CATEGORY_NAME ) )
            {
            Util.logConsole(  "categoryName >" + fieldString + "<" );
            categoryName = fieldString;

//            if ( rootAccount.getAccountByName( accountName ) == null )
//                {
//                Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= will not import it." );
////                Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= and STOP ON ERROR" );
////                throwException( "dataTypeExpecting =" + dataTypeExpecting + "=  but that account does not exist =" + fieldString + "= and STOP ON ERROR" );
//                return false; // skip this line
//                }
//            this.accountNameFromCSV = accountName;
            }
         } // end for

     // MOVED to TransactionReader so everyone creates it the same way.
//      txn.setFITxnId( date + ":" + currency.format( amount, '.' ) + ":" + description + ":" + txn.getCheckNum() + ":" + txn.getMemo() );
//      Util.logConsole(  "FITxnld >" + date + ":" + currency.format( amount, '.' ) + ":" + description + ":" + txn.getCheckNum() + ":" + txn.getMemo() + "<" );

      return true;
   }

     public Date parseDateToInt( String dateStr, String format )
     {
      Date ddd = null;
      SimpleDateFormat sdf = null;

      try {
      // convert to validate with Java date formatting d,y, and M. case matters.
      String jDateFormat = format.toLowerCase();
      jDateFormat = jDateFormat.replace( 'm', 'M' );
      sdf = new SimpleDateFormat( jDateFormat );
      sdf.setLenient( false );
      
      ddd = sdf.parse(dateStr);
      Util.logConsole(  "parseDateToInt() from format =" + format + "=  and date in string =" + dateStr + "=   got Date =" + ddd.toString() + "=" );
    }
    catch (ParseException e) {
      Util.logConsole(  "parseDateToInt() parseException =" + sdf.toString() + "=" );
      return ddd;
    }
    catch (IllegalArgumentException e) {
      Util.logConsole(  "parseDateToInt() IllegalArgumentException =" + sdf.toString() + "=" );
      return ddd;
    }
    return ddd;
    }

    public String convertMmmFormattedDate( String dateStr, String format )
     {
      Date ddd = null;
      SimpleDateFormat sdf = null;

      try {
      // convert to validate with Java date formatting d,y, and M. case matters.
      String jDateFormat = format.toLowerCase();
      jDateFormat = jDateFormat.replace( 'm', 'M' );
      sdf = new SimpleDateFormat( jDateFormat );
      sdf.setLenient( false );
      
      ddd = sdf.parse( dateStr );
      Util.logConsole(  "convertMmmFormattedDate() from format =" + format + "=  and date in string =" + dateStr + "=   got Date =" + ddd.toString() + "=" );

      jDateFormat = jDateFormat.replace( "MMM", "MM" );  // convert MMM Jan to MM number 1
      sdf = new SimpleDateFormat( jDateFormat );
      sdf.setLenient( false );
    }
    catch (ParseException e) {
      Util.logConsole(  "parseDateToInt() parseException =" + sdf.toString() + "=" );
      return dateStr;
    }
    catch (IllegalArgumentException e) {
      Util.logConsole(  "parseDateToInt() IllegalArgumentException =" + sdf.toString() + "=" );
      return dateStr;
    }
    return sdf.format( ddd );  
    }
             
    public int getIntDate( Date gotDate )
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(gotDate);
        return (cal.get( Calendar.YEAR ) * 10000) + (cal.get( Calendar.MONTH ) * 100) + cal.get( Calendar.DAY_OF_MONTH );
    }

    public String giveFormattedDate( Date ddd, String format )
    {
      StringBuffer sss = new StringBuffer();
      String jDateFormat = format.toLowerCase();
      jDateFormat = jDateFormat.replace( 'm', 'M' );
      SimpleDateFormat sdf = new SimpleDateFormat( jDateFormat );
      sdf.setLenient( false );

      if ( ddd == null )
          return "";
      
        //StringBuffer buf =  sdf.format( ddd );
      return sdf.format( ddd );  
    }
    
   @Override
   protected boolean assignDataToTxn( OnlineTxn txn ) throws IOException
    {
    txn.setAmount( amount );
    txn.setTotalAmount( amount );

    // ---  set a default date to be used if particular dates are not set  ---s
    if ( date == 0 )
        {
        if ( dateInitiated != 0 )
            {
            date = dateInitiated;
            }
        else if ( datePurchased != 0 )
            {
            date = datePurchased;
            }
        else if ( datePosted != 0 )
            {
            date = datePosted;
            }
        else if ( dateAvailable != 0 )
            {
            date = dateAvailable;
            }
        else
            {
            Util.logConsole(  "*** Error: No Date field is set !" );
            throwException( "*** Error: No Date field is set !" );
            }
        }
    
    if ( dateAvailable != 0 )
        {
        txn.setDateAvailableInt( dateAvailable );
        }
    else
        {
        txn.setDateAvailableInt( date );
        }
    
    if ( dateInitiated != 0 )
        {
        txn.setDateInitiatedInt( dateInitiated );
        }
    else
        {
        txn.setDateInitiatedInt( date );
        }
    
    if ( datePosted != 0 )
        {
        txn.setDatePostedInt( datePosted );
        }
    else
        {
        txn.setDatePostedInt( date );
        }
    
    if ( datePurchased != 0 )
        {
        txn.setDatePurchasedInt( datePurchased );
        }
    else
        {
        txn.setDatePurchasedInt( date );
        }

//    Util.logConsole(  "date >" + date + "<" );
//    Util.logConsole(  "date >" + txn.getDateAvailableInt() + "<" );
//    Util.logConsole(  "date >" + txn.getDateInitiatedInt() + "<" );
//    Util.logConsole(  "date >" + txn.getDatePostedInt() + "<" );
//    Util.logConsole(  "date >" + txn.getDatePurchasedInt() + "<" );

    txn.setCheckNum( checkNumber );
    txn.setName( description );
    txn.setMemo( memo );
    txn.setPhone( phoneString );
    txn.setSubAccountTo( categoryName );  // Hopefully this is ok to use as I do not know the MD api.
    
		// MOVED to TransactionReader so everyone creates it the same way.
//		txn.setFITxnId( date + ":" + currency.format( amount, '.' )
//				+ ":" + description + ":" + txn.getCheckNum() + ":" + txn.getMemo() );
    //Util.logConsole(  "FITxnld >" + date + ":" + currency.format( amount, '.' )
    //                        + ":" + description + ":" + txn.getCheckNum() + ":" + txn.getMemo() + "<" );
//(date == 0 ? datePurchased : date)
    return true;
    }

   @Override
   public String[] getSupportedDateFormats()
   {
      return SUPPORTED_DATE_FORMATS;
   }

   @Override
   public String getDateFormat()
   {
   Util.logConsole(  "customReader getDateFormat() >" + dateFormatStringSelected + "<" );
   return dateFormatStringSelected;
   }

   @Override
   public void setDateFormat( String format )
   {
      if ( format == null )
      {
         return;
      }

   Util.logConsole(  "setDateFormat() format =" + format + "=   dateFormatString =" + dateFormatString + "=" );
      if ( ! format.equals( dateFormatStringSelected ) )
      {
         dateFormat = new CustomDateFormat( format );
         dateFormatStringSelected = format;
      }

      /*
   dateFormatStringSelected = getCustomReaderData().getDateFormatString();
   Util.logConsole(  "customReader setDateFormat() =" + dateFormatStringSelected + "<" );
   Util.logConsole(  "customReader customReaderDialog.getDateFormatSelected() >" + getCustomReaderData().getDateFormatString() + "<" );
   dateFormat = new CustomDateFormat( getCustomReaderData().getDateFormatString() );
   */
      
      /*
      if ( !DATE_FORMAT_US.equals( format ) )
      {
         throw new UnsupportedOperationException( "Not supported yet." );
      }
       * 
       */
   }

   private static boolean findDateFormat( String[] compatibleDateFormats, String dateFormatStringArg )
   {
      if ( dateFormatStringArg == null )
      {
         return false;
      }

      for ( String s : compatibleDateFormats )
      {
         if ( s.equals( dateFormatStringArg ) )
         {
            return true;
         }
      }

      return false;
   }

//   @Override
//       public void setFieldSeparatorChar( int xxx) {
//        fieldSeparatorChar.setText( String.valueOf( Character.toString( (char) xxx ) ) );
//    }
//
//   @Override
//    public int getFieldSeparatorChar() {
//        return fieldSeparator;
//    }
    

   @Override
   protected int getHeaderCount()
   {
      return getCustomReaderData().getHeaderLines();
   }   
   
}
