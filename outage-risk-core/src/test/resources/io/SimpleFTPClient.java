package com.nokia.oss.commons.tools.outage;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by harchen on 8/27/2015.
 */
public class SimpleFTPClient
{
    public void copyFtpFile( String from, String to ) throws IOException, FileNotFoundException
    {
        InputStream is = null;
        OutputStream out = null;
        try
        {
            is = new BufferedInputStream( new FileInputStream( from ) );
            out = new BufferedOutputStream( new FileOutputStream( to ) );
            byte[] buffer = new byte[8192];
            int size = 0;
            do
            {
                size = is.read( buffer );
                if( size != -1 )
                    to.write( buffer, 0, size );
            }
            while( size != -1 );
        }
        finally
        {
            is.close();
            out.close();
        }
    }
}
