package com.nokia.oss.commons.tools.outage;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by harchen on 2015/10/13.
 */
public class IOResoucePassedIntoCommonIOUtilMethod
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
            IOUtils.closeQuietly(inputStream);

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
