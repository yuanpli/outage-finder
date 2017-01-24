package com.nokia.oss.commons.tools.outage;
import java.io.ByteArrayInputStream;

/**
 * Created by harchen on 2015/10/13.
 */
public class InMemoryIOOperation
{
    public static String toHexString( String source )
    {
        StringBuilder sb = new StringBuilder();
        ByteArrayInputStream inputStream = new ByteArrayInputStream( source.getBytes() );
        int data = -1;
        while( (data = inputStream.read()) != -1 )
        {
            sb.append( Integer.toHexString( data ) );
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(toHexString("Hello World"));
    }
}
