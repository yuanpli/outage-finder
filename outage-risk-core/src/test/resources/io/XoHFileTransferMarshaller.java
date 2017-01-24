package com.nokia.oss.commons.tools.outage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Created by harchen on 8/27/2015.
 */
public class XoHFileTransferMarshaller
{
    public static Logger LOGGER = Logger.getLogger( XoHFileTransferMarshaller.class.getName() );
    public static final int BUFFER_SIZE = 1024;

    public void saveToFile( File from, File to, boolean isZip ) throws Exception
    {
        InputStream is = null;
        OutputStream out = null;
        try
        {
            if( isZip )
            {
                is = new GZIPInputStream( new FileInputStream( from ) );
            }
            else
            {
                is = new FileInputStream( from );
            }
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            out = new BufferedOutputStream( new FileOutputStream( to ) );

            while( (len = is.read( buf )) != -1 )
            {
                if( len == 0 )
                {
                    LOGGER.log( Level.INFO, "Can't read any byte from inputStream, try one more time" );
                    // if can't read any byte before reaching EOF, try to read one more time
                    len = is.read( buf );
                    if( len == 0 )
                    {
                        String errMsg = "Can't read any byte from inputStream, download file may not be complete";
                        LOGGER.log( Level.SEVERE, errMsg );
                        throw new Exception( errMsg );
                    }
                    else if( len == -1 )
                    {
                        break;
                    }
                }
                out.write( buf, 0, len );
            }
            LOGGER.info( "File download completely" );
        }
        catch( Exception e )
        {
            LOGGER.log( Level.SEVERE, "Failed to write receiving file due to {}", e.getMessage() );
            throw e;
        }
        finally
        {
            try
            {
                is.close();
                out.close();
            }
            catch( IOException e )
            {
                LOGGER.log( Level.WARNING, "Failed to close input or output stream due to {}", e.getMessage() );
            }
        }
    }
}
