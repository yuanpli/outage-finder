package com.nokia.oss.commons.tools.outage;

import java.io.*;

/**
 * Created by harchen on 2015/10/13.
 */
public class IOResourceAsInputParameter
{
    public void close( InputStream inputStream )
    {
        if( inputStream != null )
        {
            try
            {
                inputStream.close();
            }
            catch( IOException e )
            {

            }
        }
    }
}
