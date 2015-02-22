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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author miki
 */
public class DateGuesser
{
   private static final int EOF = -1;
   private static final String D = "D";
   private static final String DD = "DD";
   private static final String M = "M";
   private static final String MM = "MM";
   private static final String YY = "YY";
   private static final String YYYY = "YYYY";
   private Map<String, Integer> results = new HashMap<String, Integer>();
   private int datesDetected = 0;
   private String[] possibleFormats;
   private double bestFormatProbability;
   private StringBuilder field1 = new StringBuilder();
   private int field1Value;
   private String field1Format;
   private int separator1;
   private StringBuilder field2 = new StringBuilder();
   private int field2Value;
   private String field2Format;
   private int separator2;
   private StringBuilder field3 = new StringBuilder();
   private int field3Value;
   private String field3Format;
   private StringBuilder format = new StringBuilder();

   public void checkDateString( String date )
   {
      try
      {
         date = date.trim();
         StringReader reader = new StringReader( date );

         separator1 = parseField( reader, field1 );
         separator2 = parseField( reader, field2 );
         int eof = parseField( reader, field3 );

         if ( field1.length() == 0 || field2.length() == 0 || field3.length() == 0 ||
            !isEof( eof ) )
         {
            return;
         }

         field1Value = Integer.parseInt( field1.toString() );
         field2Value = Integer.parseInt( field2.toString() );
         field3Value = Integer.parseInt( field3.toString() );

         if ( checkPossibleFormats() )
         {
            ++datesDetected;
         }
      }
      catch ( Exception x )
      {
         // ignore errors, simply means this is probably not a date string
      }
   }

   private boolean checkPossibleFormats()
   {
      boolean retVal = false;

      // check if its possible to be a day
      if ( field1Value >= 1 && field1Value <= 31 && field1.length() <= 2 )
      {
         if ( field1.length() == 1 )
         {
            field1Format = D;
            retVal |= checkPossibleFormats2();
         }
         else
         {
            field1Format = DD;
            retVal |= checkPossibleFormats2();
            if ( field1Value < 10 )  // >= 10 )
            {
               field1Format = D;
               retVal |= checkPossibleFormats2();
            }
         }
      }

      // check if its possible to be a month
      if ( field1Value >= 1 && field1Value <= 12 && field1.length() <= 2 )
      {
         if ( field1.length() == 1 )
         {
            field1Format = M;
            retVal |= checkPossibleFormats2();
         }
         else
         {
            field1Format = MM;
            retVal |= checkPossibleFormats2();
            if ( field1Value < 10 )  // >= 10 )
            {
               field1Format = M;
               retVal |= checkPossibleFormats2();
            }
         }
      }

      // check if its possible to be a year
      if ( field1.length() == 2 )
      {
         field1Format = YY;
         retVal |= checkPossibleFormats2();
      }
      else if ( field1.length() == 4 )
      {
         field1Format = YYYY;
         retVal |= checkPossibleFormats2();
      }

      return retVal;
   }

   private boolean checkPossibleFormats2()
   {
      boolean retVal = false;

      // check if its possible to be a day
      if ( field1Format != D && field1Format != DD )
      {
         if ( field2Value >= 1 && field2Value <= 31 && field2.length() <= 2 )
         {
            if ( field2.length() == 1 )
            {
               field2Format = D;
               retVal |= checkPossibleFormats3();
            }
            else
            {
               field2Format = DD;
               retVal |= checkPossibleFormats3();
               if ( field2Value < 10 )  // >= 10 )
               {
                  field2Format = D;
                  retVal |= checkPossibleFormats3();
               }
            }
         }
      }

      // check if its possible to be a month
      if ( field1Format != M && field1Format != MM )
      {
         if ( field2Value >= 1 && field2Value <= 12 && field2.length() <= 2 )
         {
            if ( field2.length() == 1 )
            {
               field2Format = M;
               retVal |= checkPossibleFormats3();
            }
            else
            {
               field2Format = MM;
               retVal |= checkPossibleFormats3();
               if ( field2Value < 10 )  // >= 10 )
               {
                  field2Format = M;
                  retVal |= checkPossibleFormats3();
               }
            }
         }
      }

      // check if its possible to be a year
      if ( field1Format != YY && field1Format != YYYY )
      {
         if ( field2.length() == 2 )
         {
            field2Format = YY;
            retVal |= checkPossibleFormats3();
         }
         else if ( field2.length() == 4 )
         {
            field2Format = YYYY;
            retVal |= checkPossibleFormats3();
         }
      }

      return retVal;
   }

   private boolean checkPossibleFormats3()
   {
      boolean retVal = false;

      // check if its possible to be a day
      if ( field1Format != D && field1Format != DD && field2Format != D && field2Format !=
         DD )
      {
         if ( field3Value >= 1 && field3Value <= 31 && field3.length() <= 2 )
         {
            if ( field3.length() == 1 )
            {
               field3Format = D;
               retVal = true;
               registerFormat();
            }
            else
            {
               field3Format = DD;
               retVal = true;
               registerFormat();
               if ( field3Value < 10 )  // >= 10 )
               {
                  field3Format = D;
                  registerFormat();
               }
            }
         }
      }

      // check if its possible to be a month
      if ( field1Format != M && field1Format != MM && field2Format != M && field2Format !=
         MM )
      {
         if ( field3Value >= 1 && field3Value <= 12 && field3.length() <= 2 )
         {
            if ( field3.length() == 1 )
            {
               field3Format = M;
               retVal = true;
               registerFormat();
            }
            else
            {
               field3Format = MM;
               retVal = true;
               registerFormat();
               if ( field3Value < 10 )  // >= 10 )
               {
                  field3Format = M;
                  registerFormat();
               }
            }
         }
      }

      // check if its possible to be a year
      if ( field1Format != YY && field1Format != YYYY && field2Format != YY &&
         field2Format != YYYY )
      {
         if ( field3.length() == 2 )
         {
            field3Format = YY;
            retVal = true;
            registerFormat();
         }
         else if ( field3.length() == 4 )
         {
            field3Format = YYYY;
            retVal = true;
            registerFormat();
         }
      }

      return retVal;
   }

   private void registerFormat()
   {
      clearResults();

      format.setLength( 0 );
      format.append( field1Format );
      format.appendCodePoint( separator1 );
      format.append( field2Format );
      format.appendCodePoint( separator2 );
      format.append( field3Format );

      String key = format.toString();

      Integer count = results.get( key );
      if ( count == null )
      {
          System.err.println( "saving format key =" + key + "=   count =" + 1 );
         results.put( key, 1 );
      }
      else
      {
          System.err.println( "saving format key =" + key + "=   count =" + (count + 1) );
         results.put( key, count + 1 );
      }
   }

   public String getBestFormat()
   {
      calculateResults();

      if ( possibleFormats.length > 0 )
      {
         return possibleFormats[0];
      }
      else
      {
         return null;
      }
   }

   public double getBestFormatProbability()
   {
      calculateResults();

      return bestFormatProbability;
   }

   public String[] getPossibleFormats()
   {
      calculateResults();

      return possibleFormats;
   }

   private void clearResults()
   {
      possibleFormats = null;
      bestFormatProbability = 0;
   }

   private void calculateResults()
   {
      if ( possibleFormats != null )
      { // are results already calculated?
         return;
      }

      SortedMap<Integer, String> sortedResults = new TreeMap<Integer, String>(
         new Comparator<Integer>()
         {
            public int compare( Integer o1, Integer o2 )
            {
               return o2 - o1;
            }
         } );

      for ( Map.Entry<String, Integer> entry : results.entrySet() )
      {
         System.err.println( "results before sort entry.getValue() =" + entry.getValue() + "=   entry.getKey() =" + entry.getKey() + "=" );
         sortedResults.put( entry.getValue(), entry.getKey() );
      }

      possibleFormats = new String[sortedResults.size()];
      sortedResults.values().toArray( possibleFormats );

      for ( String tmp : possibleFormats )
      {
         System.err.println( "possibleFormats =" + tmp + "=" );
      }
      
      if ( datesDetected == 0 )
      {
         bestFormatProbability = 0;
      }
      else
      {
         bestFormatProbability = sortedResults.firstKey().doubleValue() /
            (double) datesDetected;
      }
   }

   protected static final int parseField( Reader reader, StringBuilder fieldValue )
      throws IOException
   {
      fieldValue.setLength( 0 );

      if ( !reader.ready() )
      {
         return -1;
      }

      int ch;
      for ( ch = reader.read(); isDigit( ch ); ch = reader.read() )
      {
         fieldValue.appendCodePoint( ch );
      }

      return ch;
   }

   private static final boolean isDigit( int ch )
   {
      return ch >= '0' && ch <= '9';
   }

   private static final boolean isEof( int ch )
   {
      return ch == EOF;
   }
}
