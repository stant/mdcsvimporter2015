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

/**
 *
 * @author miki
 * modified by: Stan Towianski
 */
public class CSVReader
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

   public CSVReader()
      throws IOException
   {
   }
   
   /**
    * Constructs a new CSV file reader.
    * @param reader must be a valid reference to a reader providing CSV data to parse.
    * @throws java.io.IOException
    */
   public CSVReader( Reader reader, CustomReaderData customReaderData )
      throws IOException
   {
      if ( reader == null || !reader.ready() )
      {
         throw new IllegalArgumentException( "Reader must be a valid object." );
      }
      this.reader = reader;
      this.customReaderData = customReaderData;
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
   public boolean nextLine()
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
      //System.err.println( "nextField() fieldSeparator =" + (char)fieldSeparator + "=" );

      if ( isEol( lastChar ) || isEof( lastChar ) )
      {
         //System.err.println( "nextField() return null for Eol or Eof" );
         return null;
      }

      builder.setLength( 0 );

      if ( isQuote( lastChar ) )
      {
         // quoted field
         lastChar = reader.read();
         while ( !isQuote( lastChar ) && !isEol( lastChar ) && !isEof( lastChar ) )
         {
            builder.appendCodePoint( lastChar );
            lastChar = reader.read();
            //System.err.println( "lastChar =" + lastChar + "=" );
         }
         //System.err.println( "end field" );
         //System.err.println( "read field =" + builder.toString() + "=" );
         
         if ( !isQuote( lastChar ) )
         {
            throw new IOException( "Unexpected end of line." );
         }

         // skip quote
         lastChar = reader.read();
         // skip spaces
         while ( isWhitespace( lastChar ) )
         {
            lastChar = reader.read();
         }
         // and the next field separator
         if ( isFieldSeparator( lastChar ) )
         {
            lastChar = reader.read();
         }
      }
      else
      {
         // plain value
         while ( !isFieldSeparator( lastChar ) && !isEol( lastChar ) &&
            !isEof( lastChar ) )
         {
            builder.appendCodePoint( lastChar );
            lastChar = reader.read();
         }
         if ( isFieldSeparator( lastChar ) )
         {
            lastChar = reader.read();
         }
      }

      // TODO: skip separator

      if ( trimFields )
      {
         //System.err.println( "CSVReader return nextField trim =" + builder.toString().trim() + "=" );
         return builder.toString().trim();
      }
      else
      {
         //System.err.println( "CSVReader return nextField =" + builder.toString() + "=" );
         return builder.toString();
      }
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

   protected final boolean isWhitespace( int ch )
   {
      return (ch == SPACE || ch == TAB) && !isQuote( ch ) && !isFieldSeparator( ch );
   }

   protected final boolean isQuote( int ch )
   {
      return ch == quoteCharacter;
   }

   protected final boolean isFieldSeparator( int ch )
   {
      return ch == fieldSeparator;
   }

   protected final boolean isEol( int ch )
   {
      return (ch == LF || ch == CR) && !isQuote( ch ) && !isFieldSeparator( ch );
   }

   protected final boolean isEof( int ch )
   {
      return ch < 0;
   }
}
