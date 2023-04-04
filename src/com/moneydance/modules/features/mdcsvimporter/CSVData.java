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

/**
 *
 * @author miki and Stan Towianski
 */
public class CSVData
{
   private String[][] data;
   private String[][] dataErr = { { "" } };
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
      //reader.reset();  NOT SUPPORTED
   }

   public String[][] getData()
   {
      return data;
   }

   public String[][] getDataErr()
   {
      return dataErr;
   }

   public void parseIntoLines( CustomReaderData customReaderData )
      throws IOException
   {
      ArrayList<String> line = new ArrayList<String>();
      ArrayList<String[]> file = new ArrayList<String[]>();
      int fieldSeparator = customReaderData.getFieldSeparatorChar();
      int maxFoundCols = 0;
      
      if ( customReaderData != null )
        {
        reader.setFieldSeparator( fieldSeparator );
        }
      
      while ( reader.nextLine() )
        {
         for ( String s = reader.nextField(); s != null; s = reader.nextField() )
            {
            //Util.logConsole( "         line.add string =" + s + "=" );
            line.add( s );
            }

         Util.logConsole( "         line.size() =" + line.size() + "=\n" );
         if ( line.size() > maxFoundCols )
            {
            maxFoundCols = line.size();
            }
         String[] newLine = new String[ line.size() ];
         line.toArray( newLine );
         file.add( newLine );
         line.clear();
        }

      data = new String[file.size()][];
      file.toArray( data );
      Util.logConsole( "    parsed lines total =" + file.size() + "=   maxFoundCols =" + maxFoundCols );
      currentLineIndex = -1;
      currentFieldIndex = -1;      
      
      int maxr = file.size();
      dataErr = new String[maxr][];
      //Util.logConsole( " reset maxr =" + maxr );
      for ( int r = 0; r < maxr; r++ )
      {
          int maxc = maxFoundCols + 1;
          String[] newLine = new String[ maxFoundCols + 1 ];
          for ( int c = 0; c < maxc; c++ )
              {
              //Util.logConsole( " reset r =" + r + "   c =" + c );
              newLine[c] = "";
              }
          dataErr[r] = newLine;
      }

//      Util.logConsole( "PRINT OUT RESET dataErr" );
//      maxr = dataErr.length;
//      Util.logConsole( " reset maxr =" + maxr );
//      for ( int r = 0; r < maxr; r++ )
//      {
//          int maxc = dataErr[r].length;
//          Util.logConsole( " reset maxc =" + maxc );
//          for ( int c = 0; c < maxc; c++ )
//              {
//              Util.logConsole( "dataErr blank [" + r + "][" + c +"] =" + dataErr[r][c] );
//              }
//      }
   }

   public void reverseListRangeOrder( long beg, long end )
        {
        //Util.logConsole(  "hasZeroFields() ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length );
        Util.logConsole( "revLine beg: " +  beg );
        Util.logConsole( "revLine end: " +  end );
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

      //Util.logConsole(  "nextLine() ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length + "   ans =" + (currentLineIndex < data.length ? "true" : "false" ) );
      return currentLineIndex < data.length;
   }

   public boolean hasEnoughFieldsPerCurrentLine( int neededFields )
   {
      Util.logConsole(  "fieldsPerCurrentLine()   data[currentLineIndex].length + 1 =" + (data[currentLineIndex].length + 1) + " >= neededFields =" + neededFields );
      return data[currentLineIndex].length + 1 >= neededFields;
   }

   public boolean nextField()
   {
      //Util.logConsole(  "nextField() ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length );
      if ( currentLineIndex < 0 || currentLineIndex >= data.length )
      {
      //Util.logConsole(  "nextField() ----  return false" );
         return false;
      }

      if ( currentFieldIndex < data[currentLineIndex].length )
      {
         ++currentFieldIndex;
      }

      //Util.logConsole(  "nextField()2 ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length + "   ans =" + (currentFieldIndex < data[currentLineIndex].length ? "true" : "false" ) );
      return currentFieldIndex < data[currentLineIndex].length;
   }

   public boolean hasZeroFields()
   {
      //Util.logConsole(  "hasZeroFields() ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length );
      if ( currentLineIndex < 0 || currentLineIndex >= data.length )
      {
      //Util.logConsole(  "hasZeroFields() ----  return false" );
         return false;
      }

      //Util.logConsole(  "hasZeroFields()2 ----  currentLineIndex =" + currentLineIndex + "=    data.length =" + data.length + "   ans =" + (0 < data[currentLineIndex].length ? "true" : "false" ) );
      return 0 < data[currentLineIndex].length;
   }

   public String getField()
   {
      if ( currentLineIndex < 0 || currentLineIndex >= data.length )
      {
         return "";
      }
      if ( currentFieldIndex < 0 || currentFieldIndex >= data[currentLineIndex].length )
      {
         return "";
      }

      return data[currentLineIndex][currentFieldIndex];
   }

   public String getFieldErr()
   {
    //Util.logConsole( "getFieldErr current ptr [" + currentLineIndex + "][" + currentFieldIndex + "]" );
      if ( currentLineIndex < 0 || currentLineIndex >= dataErr.length )
      {
         return "";
      }
      if ( currentFieldIndex < 0 || currentFieldIndex >= dataErr[currentLineIndex].length )
      {
         return "";
      }

      return dataErr[currentLineIndex][currentFieldIndex];
   }

   public void setFieldErr( String errStr )
   {
    //Util.logConsole( "setFieldErr current ptr [" + currentLineIndex + "][" + currentFieldIndex + "]" );
      if ( currentLineIndex < 0 || currentLineIndex >= dataErr.length )
      {
         return;
      }
      if ( currentFieldIndex < 0 || currentFieldIndex >= dataErr[currentLineIndex].length )
      {
         return;
      }

      dataErr[currentLineIndex][currentFieldIndex] = errStr;
   }

   public String getFieldErr( int row, int col )
   {
    //Util.logConsole( "getFieldErr [" + row + "][" + col + "]" );
      if ( row < 0 || row >= dataErr.length )
      {
         return "";
      }
      if ( col < 0 || col >= dataErr[row].length )
      {
         return "";
      }

      return dataErr[row][col];
   }

   public void setFieldErr( int row, int col, String errStr )
   {
    //Util.logConsole( "setFieldErr [" + row + "][" + col + "]" );
      if ( row < 0 || row >= dataErr.length )
      {
          return;
      }
      if ( col < 0 || col >= dataErr[row].length )
      {
          return;
      }

      dataErr[row][col] = errStr;
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
          Util.logConsoleAppend( "currentLineIndex out of range =" + currentLineIndex );
          return "";
          }

      Util.logConsoleAppend( "\n curr line >" );
      try {
       for ( int i = 0; i < data[currentLineIndex].length; i ++ )
           {
           if ( i > 0 )
                Util.logConsoleAppend( "|" );
           Util.logConsoleAppend( data[currentLineIndex][currentFieldIndex] );
           }
        }
      catch( Exception ex )
        {
        Util.logConsoleAppend( "*** Error in printCurrentLine at currentLineIndex =" + currentLineIndex + "   currentFieldIndex =" + currentFieldIndex );
        }
       Util.logConsoleAppend( "< curr line." );
       return "";
   }

   public void printFile()
    {
    Util.logConsoleAppend( "\n ------------- PRINT FILE  ---------------" );
    int maxRows = data.length;
    for ( int row = 0; row < maxRows; row++ )
        {
        Util.logConsoleAppend( "\n line [" +  row + "] >" );
        for ( int fieldIndex = 0; fieldIndex < data[ row ].length; fieldIndex++ )
            {
            if ( fieldIndex > 0 )
                    Util.logConsoleAppend( "|" );
            Util.logConsoleAppend( data[ row ][ fieldIndex ] );
            }
        Util.logConsoleAppend( "<" );
        }
    Util.logConsoleAppend( "\n -------------  END PRINT FILE  ---------------" );
    }

    public CSVReader getReader() {
        return this.reader;
    }

    public void setReader(CSVReader reader) {
        this.reader = reader;
    }
   
}
