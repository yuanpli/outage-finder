package com.nokia.oss.commons.tools.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by harchen on 2016/3/28.
 */
public class IOResourceClosingUtilityMethod
{
    private static final Logger LOG = LoggerFactory.getLogger( IOResourceClosingUtilityMethod.class );

    public void close()
    {
        InputStream inputStream = getInputStream();
        try
        {
            if( inputStream != null )
            {
                inputStream.close();
            }
        }
        catch( IOException ignore )
        {
            LOG.warn( "Failed to close inputStream", ignore );
        }
    }
}
