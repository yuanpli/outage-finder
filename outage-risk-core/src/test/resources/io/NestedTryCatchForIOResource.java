package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created by harchen on 2016/7/19.
 */
public class NestedTryCatchForIOResource
{
    public void saveOneAdaptationFile( String adaptationFolder, URI uri ) throws FileNotFoundException, IOException
    {
        InputStream in = null;
        OutputStream out = null;
        try
        {
            File adaptationFile = new File( uri );
            File outputFile = new File( adaptationFolder + adaptationFile.getName() );
            in = new FileInputStream( adaptationFile );
            out = new FileOutputStream( outputFile );
            long temp = adaptationFile.length();
            int size = Integer.parseInt( Long.toString( temp ) );
            byte[] buffer = new byte[size];
            int bufferLength;
            while( (bufferLength = in.read( buffer )) > 0 )
            {
                out.write( buffer, 0, bufferLength );
            }
        }
        finally
        {
            try
            {
                if( in != null )
                {
                    in.close();
                }
            }
            finally
            {
                if( out != null )
                {
                    out.close();
                }
            }

        }
    }
}
