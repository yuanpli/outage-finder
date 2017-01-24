/* ========================================== */
/* � 2015 Nokia Solutions and Networks. */
/* All rights reserved. */
/* ========================================== */

package com.nokia.oss.neprov.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import com.nokia.oss.neprov.constants.NEProvErrorCodes;
import com.nokia.oss.neprov.exception.NEProvisionException;
import com.nsn.oss.interfaces.extendedlogger.ExtLogger;

public final class ArchiveCreator
{

    private static final int BYTEARRAY_INITIAL_BUFFER_SIZE = 1024;

    private static final ExtLogger<?> LOGGER =
        ExtLogger.getLogger( ArchiveCreator.class.getPackage(), ArchiveCreator.class.getName() );

    // Utility Class
    private ArchiveCreator()
    {
    }

    /**
     * Creates a tar archive containing all entries passed as arguments. The tar is not written to disk, instead the
     * method returns raw tar bytes.
     * 
     * @param files map containing raw file data mapped to filename
     * @return byte array for the tar
     * @throws NEProvisionException If tar creation fails due to IO exceptions.
     */
    public static byte[] archiveFilesToTAR( Map<String, byte[]> files ) throws NEProvisionException
    {
        ByteArrayOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        TarArchiveOutputStream tOut = null;

        try
        {
            fOut = new ByteArrayOutputStream( BYTEARRAY_INITIAL_BUFFER_SIZE );
            bOut = new BufferedOutputStream( fOut );
            tOut = new TarArchiveOutputStream( bOut );

            for( Entry<String, byte[]> fileEntry : files.entrySet() )
            {
                createTarEntry( tOut, fileEntry.getKey(), fileEntry.getValue() );
            }
            tOut.finish();
        }
        catch( IOException e )
        {
            throw new NEProvisionException( "Failed to create TAR", e, NEProvErrorCodes.CREATE_TAR_FAILURE );
        }
        finally
        {
            closeStream( tOut );
        }
        return fOut.toByteArray();
    }

    private static void createTarEntry( TarArchiveOutputStream tOut, String fileName, byte[] data ) throws IOException
    {
        if( data != null )
        {
            TarArchiveEntry tarEntry = new TarArchiveEntry( fileName );
            tarEntry.setSize( data.length );

            tOut.putArchiveEntry( tarEntry );
            tOut.write( data );
            tOut.flush();
            tOut.closeArchiveEntry();
        }
    }

    private static void closeStream( Closeable stream )
    {
        if( stream != null )
        {
            try
            {
                stream.close();
            }
            catch( IOException e )
            {
                LOGGER.warning( "Failed to close Stream", e );
            }
        }

    }

}
