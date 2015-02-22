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
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author miki
 * modified by: Stan Towianski
 */
public class RegexReader extends CSVReader
{
   /**
    * Carriage-Return
    */
   private static final int CR = 13;
   /**
    * Line-Feed
    */
   private static final int LF = 10;
   /**
    * Space
    */
   private static final int SPACE = 32;
   /**
    * Tab
    */
   private static final int TAB = 8;
   /**
    * Character used as a separator of individual fields.
    */
   private int fieldSeparator = ',';
   /**
    * Character used to start and end quoted sequences.
    */
   private int quoteCharacter = '"';
   /**
    * Character used to mark comment lines.
    */
   private int commentCharacter = '#';
   /**
    * Character used to mark pragma lines.
    */
   private int pragmaCharacter = '$';
   /**
    * True if the fields values should be trimmed before return. Equivalent of returning
    * nextField().trim().
    */
   private boolean trimFields = true;
   /**
    * True if empty lines should be skipped and not reported as data rows.
    */
   private boolean skipEmptyLines = false;
   /**
    * Reference to the reader.
    */
   private Reader reader;
   private CustomReaderData customReaderData;
   /**
    * The last char read from the reader. Also it stores the next character to be parsed.
    * &lt;0 if end of file is reached. Code is currently written so that initializing
    * this to LF is the proper way to start parsing.
    */
   private int lastChar = LF;
   /**
    * Temporary buffer used to build field values before hey are returned.
    */
   private StringBuilder builder = new StringBuilder();

   private LineNumberReader lineReader;
   private String rgLine = "";
   private int rgFieldCnt = 0;
   
   public RegexReader()
      throws IOException
   {
   }
   
   /**
    * Constructs a new CSV file reader.
    * @param reader must be a valid reference to a reader providing CSV data to parse.
    * @throws java.io.IOException
    */
   public RegexReader( Reader reader, CustomReaderData customReaderData )
      throws IOException
   {
      if ( reader == null || !reader.ready() )
      {
         throw new IllegalArgumentException( "Reader must be a valid object." );
      }
      this.reader = reader;
      this.customReaderData = customReaderData;
      lineReader = new LineNumberReader( reader );
   }

   /**
    * Closes the input reader and releases all object references. No other calls to this
    * instance should be made.
    * @throws java.io.IOException IOException might be thrown by referenced reader. See
    * Reader.close().
    */
   public void close()
      throws IOException
   {
      reader.close();
      reader = null;
      lastChar = -1;
   }

   /**
    * Used to move to the next line in the CSV file. It must be called before the each
    * line is processed, including before the very first line in the file. Any fields on
    * the current line that have not been retrieved, will be skipped.
    * @return true if the file contains another line.
    * @throws java.io.IOException if data cannot be read.
    */
   public boolean nextLine_HIDE()
      throws IOException
   {
      while ( nextField() != null )
      {
      }

      // skip EOL; possible combinations are CR, CR+LF, LF
      if ( lastChar == CR )
      {
         lastChar = reader.read();
      }
      if ( lastChar == LF )
      {
         lastChar = reader.read();
      }

      // skip whitespace at the beginning
      if ( trimFields )
      {
         while ( isWhitespace( lastChar ) && !isEof( lastChar ) )
         {
            lastChar = reader.read();
         }
      }

      // skip comment lines
      if ( lastChar == commentCharacter )
      {
         do
         {
            lastChar = reader.read();
         } while ( !isEof( lastChar ) && lastChar != CR && lastChar != LF );
         return nextLine();
      }

      // handle pragma lines
      if ( lastChar == pragmaCharacter )
      {
         throw new IOException( "Pragma lines (starting with " + pragmaCharacter +
            ") are currently not supported. If you need to use this character surround " +
            "the field with quotes." );
      }

      // skip empty lines if so requested
      if ( skipEmptyLines && isEol( lastChar ) )
      {
         return nextLine();
      }

      // end of file
      if ( isEof( lastChar ) )
      {
         return false;
      }
      return true;
   }

   /**
    * Retrieves next field on the current line. If the field value was quoted, the quotes
    * are stripped. If the reader has been configured to trim fields, then all whitespaces
    * at the beginning and end of the field are stripped before returning.
    * @return field value or null if no more fields on the current line.
    * @throws java.io.IOException if data cannot be read.
    */
   public String nextField()
      throws IOException
   {
    //Pattern and Matcher are used here, not String.matches(regexp),
    //since String.matches(regexp) would repeatedly compile the same
    //regular expression
    //String pat42 =   "([^,]*([,]|\\Z)).*";
    //String pat5 =   "Check[ ]#(\\d*)[^,]*|([^,]*)([,]|\\Z).*";
       /* was used
    String pat42 =   "([^,]*([,]|\\Z)).*";
    String pat5 =   "(?:Check[ ]#(\\d*)|([^,]*)([,]|\\Z)).*";

    Pattern regexp = Pattern.compile( pat42 );
    Pattern regexp2 = Pattern.compile( pat5 );
    */
       
    ArrayList<Matcher> matcherAl = new ArrayList<Matcher>();
    /*
    matcherAl.add( regexp.matcher("") );
    matcherAl.add( regexp.matcher("") );
    matcherAl.add( regexp2.matcher("") );
    matcherAl.add( regexp.matcher("") );
    matcherAl.add( regexp.matcher("") );
    matcherAl.add( regexp.matcher("") );
    matcherAl.add( regexp.matcher("") );
    Matcher matcher = regexp.matcher("");
    */
    for ( String patString : customReaderData.getRegexsList() )
        {
        matcherAl.add( Pattern.compile( patString ).matcher("") );
        //System.err.println( "patString =" + patString + "=" );
        }
    Matcher matcher = matcherAl.get( 0 );

    String item = null;
    
    //System.err.println( "\nnextField() fieldSeparator =" + (char)fieldSeparator + "=" );

//      if ( isEol( lastChar ) || isEof( lastChar ) )
//      {
//         //System.err.println( "nextField() return null for Eol or Eof" );
//         return null;
//      }

        if ( ! rgLine.isEmpty() )
            {
            System.err.println( "\n----- left =" + rgLine + "=   use regex =" + matcherAl.get( rgFieldCnt ).pattern() + "=" );
            matcher = (matcherAl.get( rgFieldCnt ));
            matcher.reset( rgLine ); //reset the input
            if ( matcher.matches() )
                {
                //System.err.println("Num groups: " + matcher.groupCount());
                item = matcher.group(1) == null ? "" : matcher.group(1);
                rgLine = rgLine.substring( item.length() );
                if ( item.endsWith( "," ) )
                    item = item.substring( 0, item.length() - 1 );
                System.err.println( "rgFieldCnt =" + rgFieldCnt + "   item >" + item + "<    item2 >" + matcher.group(2) + "<" );
                }
            else 
                {
                System.err.println("Input does not match pattern.");
                rgLine = "";
                return null;
                }
            rgFieldCnt++;
            }
        else
            {
            System.err.println( "No more fields left." );
            rgLine = "";
            return null;
            }

      // TODO: skip separator

      if ( trimFields )
      {
         System.err.println( "RegexReader return nextField trim =" + item.trim() + "=" );
         return item.trim();
      }
      else
      {
         System.err.println( "RegexReader return nextField =" + item + "=" );
         return item;
      }
   }

   public boolean nextLine()
      throws IOException
    //public void regexParseIntoLines(String aFileName) 
   {
    //Path path = Paths.get(aFileName);
    try 
        //(
      //BufferedReader reader = Files.newBufferedReader(path, ENCODING);
      //LineNumberReader lineReader = new LineNumberReader( reader );
    //)
    {
      System.err.println( "entered RegexReader.nextLine()" );
      if ((rgLine = lineReader.readLine()) != null) 
        {
        System.err.println( "\n---------- line =" + rgLine + "=" );
        rgFieldCnt = 0;
        return true;
      }      
    }    
    catch (IOException ex){
      ex.printStackTrace();
    }
    return false;
  }
  
   public void setFieldSeparator( int fieldSeparator )
   {
      //System.err.println( "CSVReader.setFieldSeparator =" + (char)fieldSeparator + "=" );
      this.fieldSeparator = fieldSeparator;
   }

   public int getFieldSeparator()
   {
      return fieldSeparator;
   }

   public void setQuoteCharacter( int quoteCharacter )
   {
      this.quoteCharacter = quoteCharacter;
   }

   public int getQuoteCharacter()
   {
      return quoteCharacter;
   }

   public void setCommentCharacter( int commentCharacter )
   {
      this.commentCharacter = commentCharacter;
   }

   public int getCommentCharacter()
   {
      return commentCharacter;
   }

   public void setPragmaCharacter( int pragmaCharacter )
   {
      this.pragmaCharacter = pragmaCharacter;
   }

   public int getPragmaCharacter()
   {
      return pragmaCharacter;
   }

   public void setTrimFields( boolean trimFields )
   {
      this.trimFields = trimFields;
   }

   public boolean getTrimFields()
   {
      return trimFields;
   }

   public void setSkipEmptyLines( boolean skipEmptyLines )
   {
      this.skipEmptyLines = skipEmptyLines;
   }

   public boolean getSkipEmptyLines()
   {
      return skipEmptyLines;
   }

}
