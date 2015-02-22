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

import com.moneydance.apps.md.model.OnlineTxn;
import com.moneydance.modules.features.mdcsvimporter.CSVData;
import com.moneydance.modules.features.mdcsvimporter.DateGuesser;
import com.moneydance.modules.features.mdcsvimporter.TransactionReader;
import com.moneydance.util.CustomDateFormat;
import com.moneydance.util.StringUtils;
import java.io.IOException;

/**
 *
 * @author miki
 */
public class SimpleCreditDebitReader
   extends TransactionReader
{
   private static final String DATE = "date";
   private static final String DESCRIPTION = "description";
   private static final String CREDIT = "credit";
   private static final String DEBIT = "debit";
   private CustomDateFormat dateFormat;
   private String[] compatibleDateFormats;
   private String dateFormatString;
   private String dateString;
   private String description;
   private String debit;
   private String credit;

   @Override
   public boolean canParse( CSVData data )
   {
      //data.reset();
        try {
            data.parseIntoLines( 0 );
            } 
        catch ( IOException ex ) 
            {
            //Logger.getLogger(CustomReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
            }

      boolean retVal = data.nextLine() &&
         data.nextField() && DATE.equals( data.getField().toLowerCase() ) &&
         data.nextField() && DESCRIPTION.equals( data.getField().toLowerCase() ) &&
         data.nextField() && CREDIT.equals( data.getField().toLowerCase() ) &&
         data.nextField() && DEBIT.equals( data.getField().toLowerCase() ) &&
         !data.nextField();

      // find guessable date formats
      if ( retVal )
      {
         DateGuesser guesser = new DateGuesser();
         while ( data.nextLine() )
         {
            if ( data.nextField() )
            {
               guesser.checkDateString( data.getField() );
            }
         }

         compatibleDateFormats = guesser.getPossibleFormats();
         if ( dateFormatString == null ||
            !find( compatibleDateFormats, dateFormatString ) )
         {
            setDateFormat( guesser.getBestFormat() );
         }
      }

      return retVal;
   }

   @Override
   public String getFormatName()
   {
      return "Simple Date/Description/Credit/Debit";
   }
   
   @Override
   protected boolean parseNext()
      throws IOException
   {
      csvData.nextField();
      dateString = csvData.getField();
      if ( dateString == null || dateString.length() == 0 )
      { // empty line
         return false;
      }

      csvData.nextField();
      description = csvData.getField();

      csvData.nextField();
      credit = csvData.getField();

      csvData.nextField();
      debit = csvData.getField();
      if ( credit == null && debit == null )
      {
         throwException( "Invalid line." );
      }
      
      if ( credit.length() == 0 && debit.length() == 0 )
      {
         throwException( "Credit and debit fields are both empty." );
      }

	  return true;
   }

   @Override
   protected boolean assignDataToTxn( OnlineTxn txn ) throws IOException
   {
	  long amount = 0;
      try
      {
         double amountDouble;
         if ( credit.length() > 0 )
         {
            amountDouble = StringUtils.parseDoubleWithException( credit, '.' );
         }
         else
         {
            amountDouble = -StringUtils.parseDoubleWithException( debit, '.' );
         }
         amount = currency.getLongValue( amountDouble );
      }
      catch ( Exception x )
      {
         throwException( "Invalid amount." );
      }

      int date = dateFormat.parseInt( dateString );

      txn.setAmount( amount );
      txn.setTotalAmount( amount );
      txn.setMemo( description );
      txn.setFITxnId( date + ":" + currency.format( amount, '.' ) + ":" + description );
      txn.setDatePostedInt( date );
      txn.setDateInitiatedInt( date );
      txn.setDateAvailableInt( date );

	  return true;
   }
   @Override
   public String[] getSupportedDateFormats()
   {
      return compatibleDateFormats;
   }

   @Override
    public void setSupportedDateFormats( String[] supportedDateFormats ) 
        {
        compatibleDateFormats = supportedDateFormats;
        }

   @Override
   public String getDateFormat()
   {
      return dateFormatString;
   }

   @Override
   public void setDateFormat( String format )
   {
      if ( format == null )
      {
         return;
      }

      if ( !format.equals( dateFormatString ) )
      {
         dateFormat = new CustomDateFormat( format );
         dateFormatString = format;
      }
   }

   private static boolean find( String[] compatibleDateFormats, String dateFormatString )
   {
      if ( dateFormatString == null )
      {
         return false;
      }

      for ( String s : compatibleDateFormats )
      {
         if ( dateFormatString.equals( dateFormatString ) )
         {
            return true;
         }
      }

      return false;
   }

   @Override
   protected int getHeaderCount()
   {
      return 1;
   }
}
