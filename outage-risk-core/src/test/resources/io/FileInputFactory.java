package com.nsn.oss.cm.mml.download.command.reader.format.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nsn.oss.cm.mml.download.command.CommandException;
import com.nsn.oss.cm.mml.download.command.reader.IInputFactory;

/**
 * @file FileInputFactory.java
 * @copy Copyright Nokia Siemens Networks Ltd. 2008
 * @version
 * @date 2008-05-08
 * @author Radoslaw Musial (Radzio)
 *
 */
public class FileInputFactory
    implements IInputFactory
{

    private Map name2out;

    public FileInputFactory( final String relativePath )
    {
        this.name2out = new HashMap();
    }

    @Override
    public void close() throws CommandException
    {
        Exception exception = null;
        for( final Iterator i = name2out.values().iterator(); i.hasNext(); )
        {
            final InputStream in = (InputStream)i.next();
            try
            {
                in.close();
            }
            catch( final IOException e )
            {
                exception = e;
            }
        }
        if( exception != null )
        {
            throw new CommandException( exception );
        }
    }

}
