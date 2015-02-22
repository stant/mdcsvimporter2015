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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 *
 * @author miki
 */
public class SecureFileDeleter
{
   private static final int CHUNK_SIZE = 65536;

   /**
    * Securely deletes the specified file. This is done by overwriting the file three
    * times, by 0xFF's, random values and 0's, and finally deleting the file.
    * @param file File to delete.
    * @throws java.io.IOException IOException is thrown if file does not exist, if it is a directory,
    * cannot be written to or if any other IO erroroccurs.
    */
   public static void delete( File file )
      throws IOException
   {
      if ( !file.exists() || !file.isFile() || !file.canWrite() )
      {
         throw new IOException( "Unable to securely delete specified file." );
      }

      Random random = new Random();
      byte[] buffer = new byte[CHUNK_SIZE];

      // fill file with 0xFF
      for ( int i = 0; i < CHUNK_SIZE; ++i )
      {
         buffer[i] = (byte) 0xFF;
      }
      long fileLength = file.length();
      OutputStream output = new FileOutputStream( file );
      while ( fileLength > 0 )
      {
         int chunkSize = fileLength > CHUNK_SIZE ? CHUNK_SIZE : (int) fileLength;
         output.write( buffer, 0, chunkSize );
         fileLength -= chunkSize;
      }
      output.close();

      // fill file with random values
      fileLength = file.length();
      output = new FileOutputStream( file );
      while ( fileLength > 0 )
      {
         int chunkSize = fileLength > CHUNK_SIZE ? CHUNK_SIZE : (int) fileLength;
         random.nextBytes( buffer );
         output.write( buffer, 0, chunkSize );
         fileLength -= chunkSize;
      }
      output.close();

      // fill file with 0's
      for ( int i = 0; i < CHUNK_SIZE; ++i )
      {
         buffer[i] = (byte) 0;
      }
      fileLength = file.length();
      output = new FileOutputStream( file );
      while ( fileLength > 0 )
      {
         int chunkSize = fileLength > CHUNK_SIZE ? CHUNK_SIZE : (int) fileLength;
         random.nextBytes( buffer );
         output.write( buffer, 0, chunkSize );
         fileLength -= chunkSize;
      }
      output.close();

      if ( !file.delete() )
      {
         throw new IOException( "Failed to delete file." );
      }
   }
}
