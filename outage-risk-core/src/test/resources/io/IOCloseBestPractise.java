package com.nokia.oss.outage.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by harchen on 2016/6/22.
 */
public class IOCloseBestPractise
{
    private static Logger LOG = LoggerFactory.getLogger( IOCloseBestPractise.class );

    public byte[] contentOf( File file, int length )
    {
        byte[] data = new byte[length];
        InputStream in = null;
        try
        {
            in = new FileInputStream( "foo.txt" );
            in.read( data );
            in.close();
        }
        catch( Exception e )
        {
            LOG.error( "Failed to read file ", e );
        }
        finally
        {
            IOUtils.closeQuietly( in );
        }
        return data;
    }

    public byte[] getBytes( File file, int length ) throws IOException
    {
        byte[] data = new byte[length];
        InputStream in = null;
        try
        {
            in = new FileInputStream( "foo.txt" );
            in.read( data );
            in.close();
        }
        catch( Exception e )
        {
            LOG.error( "Failed to read file ", e );
        }
        finally
        {
            if( in != null )
            {
                in.close();
            }
        }
        return data;
    }
}
