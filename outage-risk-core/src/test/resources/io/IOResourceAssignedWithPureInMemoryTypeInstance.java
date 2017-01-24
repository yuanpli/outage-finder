package com.nokia.oss.commons.tools.outage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by harchen on 2015/10/15.
 */
public class IOResourceAssignedWithPureInMemoryTypeInstance
{
    public static String toHexString( String source )
    {
        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();
        try
        {
            inputStream = new BufferedInputStream(new ByteArrayInputStream( source.getBytes() ));
            int data = -1;
            while( (data = inputStream.read()) != -1 )
            {
                sb.append( Integer.toHexString( data ) );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void main( String[] args )
    {
        System.out.println( toHexString( "Hello World" ) );
    }
}
