package com.nokia.oss.commons.tools.outage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by harchen on 8/26/2015.
 */
public class ObservationBean
{
    public String readFromFile( File file ) throws XoHException
    {
        StringBuilder data = new StringBuilder();
        String newLine = System.getProperty( "line.separator" );
        FileInputStream fin = null;
        BufferedReader input = null;
        String line;
        try
        {
            fin = new FileInputStream( file );
            input = new BufferedReader( new InputStreamReader( fin ) );
            while( (line = input.readLine()) != null )
            {
                data.append( line + newLine );
            }
        }
        catch( Exception e )
        {
            LOGGER.error( "Eror reading data from file:" + file.getName() + e, e );
            throw new XoHException( "Eror reading data from file-->" + file.getName(), e );
        }
        finally
        {
            try
            {
                if( input != null )
                {
                    input.close();
                }
                if( fin != null )
                {
                    fin.close();
                }
            }
            catch( Exception e )
            {
                LOGGER.error( "Eror closing filestreams", e );
            }

        }
        return data.toString();
    }
}
