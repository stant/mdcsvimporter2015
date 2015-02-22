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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author miki and Stan Towianski
 */
public class CSVData
{
   private String[][] data;
   private int currentLineIndex = -1;
   private int currentFieldIndex = -1;

   public CSVReader reader;
   
   public CSVData( CSVReader readerArg )
   {
       this.reader = readerArg;
   }

   public void reset()
   {
      currentLineIndex = -1;
      currentFieldIndex = -1;
   }

   public String[][] getData()
   {
      return data;
   }

   public void parseIntoLines( CustomReaderData customReaderData )
      throws IOException
   {
      ArrayList<String> line = new ArrayList<String>();
      ArrayList<String[]> file = new ArrayList<String[]>();
      int fieldSeparator = customReaderData.getFieldSeparatorChar();

      if ( customReaderData != null )
        {
        reader.setFieldSeparator( fieldSeparator );
        }
      
      while ( reader.nextLine() )
        {
         for ( String s = reader.nextField(); s != null; s = reader.nextField() )
            {
            System.err.println( "         line.add string =" + s + "=" );
            line.add( s );
            }

         System.err.println( "         line.size() =" + line.size() + "=\n" );
         String[] newLine = new String[ line.size() ];
         line.toArray( newLine );
         file.add( newLine );
         line.clear();
        }

      data = new String[file.size()][];
      file.toArray( data );
      System.err.println( "    parsed lines total =" + file.size() + "=" );
      currentLineIndex = -1;
      currentFieldIndex = -1;      
   }

   public void reverseListRangeOrder( long beg, long end )
        {
        //System.err.println(  "hasZeroFields() ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length );
        System.err.println( "revLine beg: " +  beg );
        System.err.println( "revLine end: " +  end );
        if ( end <= beg )
            {
            return;
            }
        
        int begInt = (int)beg;
        int endInt = (int)end;
       
        String[] strArr = null;
       
        for (      ; endInt > begInt; endInt--, begInt++ )
                {
                strArr = data[ endInt ];
                data[ endInt ] = data[ begInt ];
                data[ begInt ] = strArr;
                }
    }

   public boolean nextLine()
   {
      if ( currentLineIndex < data.length )
      {
         ++currentLineIndex;
         currentFieldIndex = -1;
      }

      //System.err.println(  "nextLine() ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length + "   ans =" + (currentLineIndex < data.length ? "true" : "false" ) );
      return currentLineIndex < data.length;
   }

   public boolean hasEnoughFieldsPerCurrentLine( int neededFields )
   {
      //System.err.println(  "fieldsPerCurrentLine()   data[currentLineIndex].length + 1 =" + (data[currentLineIndex].length + 1)  );
      return data[currentLineIndex].length + 1 >= neededFields;
   }

   public boolean nextField()
   {
      //System.err.println(  "nextField() ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length );
      if ( currentLineIndex < 0 || currentLineIndex >= data.length )
      {
      //System.err.println(  "nextField() ----  return false" );
         return false;
      }

      if ( currentFieldIndex < data[currentLineIndex].length )
      {
         ++currentFieldIndex;
      }

      //System.err.println(  "nextField()2 ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length + "   ans =" + (currentFieldIndex < data[currentLineIndex].length ? "true" : "false" ) );
      return currentFieldIndex < data[currentLineIndex].length;
   }

   public boolean hasZeroFields()
   {
      //System.err.println(  "hasZeroFields() ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length );
      if ( currentLineIndex < 0 || currentLineIndex >= data.length )
      {
      //System.err.println(  "hasZeroFields() ----  return false" );
         return false;
      }

      //System.err.println(  "hasZeroFields()2 ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length + "   ans =" + (0 < data[currentLineIndex].length ? "true" : "false" ) );
      return 0 < data[currentLineIndex].length;
   }

   public String getField()
   {
      if ( currentLineIndex < 0 || currentLineIndex >= data.length )
      {
         return null;
      }
      if ( currentFieldIndex < 0 || currentFieldIndex >= data[currentLineIndex].length )
      {
         return null;
      }

      return data[currentLineIndex][currentFieldIndex];
   }

   public int getCurrentLineIndex()
        {
        return currentLineIndex;
        }
   
   public int getCurrentFieldIndex()
        {
        return currentFieldIndex;
        }
   
   public int getCurrentLineIndexWithinBounds()
        {
        if ( currentLineIndex < 0 )
            {
            return 0;
            }
        if ( currentLineIndex >= data.length )
            {
            return data.length - 1;
            }
        return currentLineIndex;
        }
   
   public int getCurrentFieldIndexWithinBounds()
        {
        if ( currentFieldIndex < 0 )
            {
            return 0;
            }
        if ( currentFieldIndex >= data[currentLineIndex].length )
            {
            return data[currentLineIndex].length - 1;
            }
        return currentFieldIndex;
        }
   
   public String printCurrentLine()
   {
      if ( currentLineIndex < 0 || currentLineIndex >= data.length )
          {
          System.err.append( "currentLineIndex out of range =" + currentLineIndex );
          return null;
          }

      System.err.append( "\n curr line >" );
      try {
       for ( int i = 0; i < data[currentLineIndex].length; i ++ )
           {
           if ( i > 0 )
                System.err.append( "|" );
           System.err.append( data[currentLineIndex][currentFieldIndex] );
           }
        }
      catch( Exception ex )
        {
        System.err.append( "*** Error in printCurrentLine at currentLineIndex =" + currentLineIndex + "   currentFieldIndex =" + currentFieldIndex );
        }
       System.err.append( "< curr line." );
       return null;
   }

   public void printFile()
    {
    System.err.append( "\n ------------- PRINT FILE  ---------------" );
    int maxRows = data.length;
    for ( int row = 0; row < maxRows; row++ )
        {
        System.err.append( "\n line [" +  row + "] >" );
        for ( int fieldIndex = 0; fieldIndex < data[ row ].length; fieldIndex++ )
            {
            if ( fieldIndex > 0 )
                    System.err.append( "|" );
            System.err.append( data[ row ][ fieldIndex ] );
            }
        System.err.append( "<" );
        }
    System.err.append( "\n -------------  END PRINT FILE  ---------------" );
    }

    public CSVReader getReader() {
        return this.reader;
    }

    public void setReader(CSVReader reader) {
        this.reader = reader;
    }
   
}
