package com.nokia.oss.commons.tools.outage;

import java.io.*;

/**
 * Created by harchen on 2015/10/13.
 */
public class NewIOResourceAsReturnValue
{
    public InputStream openInputStream( File file ) throws FileNotFoundException
    {
        return new FileInputStream(file);
    }
}
