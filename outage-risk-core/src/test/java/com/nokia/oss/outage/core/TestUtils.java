package com.nokia.oss.outage.core;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by harchen on 2015/10/21.
 */
public class TestUtils
{
    public static File getTestResource( String sourceFile )
    {
        File root = Paths.get( "src", "test", "resources" ).toFile();
        return new File( root, sourceFile );
    }
}
