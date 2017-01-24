package com.nokia.oss.commons.tools.outage;
import java.io.InputStream;


/**
 * Created by harchen on 8/27/2015.
 */
public final class RealignHelper
{
    public static String getRequestMessage( String ipaddress, String seqNo, long maxEntries, String communityString )
    {
        LOG.info( "Entered getRequestMessage:" + GET_REQUEST_FILE_NAME );
        String requestMessage = null;
        try
        {
            StringBuffer getRequestBuffer = new StringBuffer();

            InputStream stream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream( "conf/" + GET_REQUEST_FILE_NAME );
            LOG.info( "stream 1" + stream );
            if( stream == null )
            {
                stream = Thread
                    .currentThread().getContextClassLoader().getResourceAsStream( "conf/" + GET_REQUEST_FILE_NAME );
            }
            LOG.info( "stream 2" + stream );
            int ch;
            while( (ch = stream.read()) != -1 )
            {
                getRequestBuffer.append( (char)ch );
            }
            String requestMessageWithPlaceHolders = getRequestBuffer.toString();
            LOG.info( "requestMessageWithPlaceHolders=" + requestMessageWithPlaceHolders );
            requestMessage =
                replacePlaceHolders( requestMessageWithPlaceHolders, ipaddress, seqNo, maxEntries, communityString );
        }
        catch( Exception e )
        {
            LOG.info( "Exception while creating request message:" + StackTracer.getStackTrace( e ) );
        }
        LOG.info( "method 'getRequestMessage' exit" );
        return requestMessage;
    }
}
