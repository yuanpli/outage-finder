package com.nokia.oss.commons.tools.outage;

import java.io.FileWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by harchen on 2015/10/15.
 */
public class IOResourceClosingInImbededTryBLock
{
    public void writeFileToDir( String content, String destinationDir, String dn, String fbFileName )
    {
        try
        {
            FileWriter fw = null;
            if( content != null )
            {
                try
                {
                    LOG.info( "dn===" + dn + "===destinationDir=" + destinationDir );
                    LOG.info( "feedback content==" + content );
                    String fileName = destinationDir + "/" + fbFileName;
                    fw = new FileWriter( fileName );
                    fw.write( content );
                }
                catch( Exception e )
                {
                    LOG.error( "Exception while writing final feedback in destination directory", e );
                }
                finally
                {
                    if( fw != null )
                    {
                        fw.close();
                    }
                }
            }
        }
        catch( IOException e )
        {
            LOG.error( "Exception while writing HW information in intermediate file", e );
        }
    }
}
