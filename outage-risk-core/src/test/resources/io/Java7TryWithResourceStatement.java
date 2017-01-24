package com.nokia.oss.commons.tools.outage;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by harchen on 2016/4/12.
 */
public class Java7TryWithResourceStatement
{
    public List<String> readLines( File file ) throws IOException
    {
        List<String> lines = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader( new FileReader( file ) ))
        {
            String line = null;
            while( (line = reader.readLine()) != null )
            {
                lines.add( line );
            }
        }
        return lines;
    }
}
