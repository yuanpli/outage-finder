package com.nokia.oss.commons.tools.outage;

import java.io.*;

/**
 * Created by harchen on 2015/10/12.
 */
public class FileOperation
{
    public void copyFile( File src, File dest )
    {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try
        {
            inputStream = new FileInputStream( src );
            outputStream = new FileOutputStream( dest );
            byte[] buffer = new byte[1024];
            int count = 0;
            while( (count = inputStream.read( buffer )) > 0 )
            {
                outputStream.write( buffer, 0, count );
            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            if( inputStream != null )
            {
                try
                {
                    inputStream.close();

                }
                catch( IOException e )
                {
                    e.printStackTrace();
                }
            }

            if( outputStream != null )
            {
                try
                {
                    outputStream.close();
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
