package com.nokia.oss.commons.tools.outage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by harchen on 2015/10/13.
 */
public class IOResourceChain
{
    private static final Logger LOG = LoggerFactory.getLogger( IOResourceChain.class );

    public BufferedReader getBufferedReaderFromFile( String fileName ) throws IOException
    {
        LOG.debug( "Entering method getBufferedReaderFromFile()" );
        InputStreamReader inStream = null;
        BufferedReader br = null;
        try
        {
            inStream = new InputStreamReader(new DataInputStream(new FileInputStream( fileName )));
            br = new BufferedReader( inStream );
        }
        catch( IOException e )
        {
            LOG.error( "FileNotFound Exception while parsing VT response", e );
            throw e;
        }
        LOG.debug( "Exiting method getBufferedReaderFromFile()" );
        return br;
    }
}
