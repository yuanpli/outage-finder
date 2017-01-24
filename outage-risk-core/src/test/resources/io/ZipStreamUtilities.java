package com.nsn.oss.cm.mmlupload.common.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;

import com.nsn.oss.cm.mmlupload.exceptions.ExceptDescription;
import com.nsn.oss.cm.mmlupload.exceptions.GeneralException;

/**
 * Class provide storage for ZipEntries from Zip file. 
 * It is workaround for a Sun bug described below.
 * 
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4079029
 * ZipEntry size can be checked after ZipEntry is read-out or it is skipped to
 * next one.
 * 
 * @author Dariusz Szuamcher
 *
 */
public class ZipStreamUtilities {
    
    private Log log = LogHelper.getLogger();
    
    private Map<String, ZipEntry> zipEntries = null;
    
    private File file = null;
    
    public ZipStreamUtilities() {
        super();
    }
    
    public ZipStreamUtilities(final File file) {
        this();
        this.file = file;
    }
    
    private Map<String, ZipEntry> readZipEntries() throws GeneralException  {
        Map<String, ZipEntry> tempZipEntries = new LinkedHashMap<String, ZipEntry>();
        ZipEntry zentry;
        ZipInputStream zis = getZipInputStream();
        try {
            while ((zentry = zis.getNextEntry()) != null) {
                tempZipEntries.put(zentry.getName(), zentry);
            }
            zis.close();
        } catch (IOException e) {
            ExceptDescription excDesc = new ExceptDescription();
            throw new GeneralException(excDesc, e);
        }
        return new HashMap<String, ZipEntry>(tempZipEntries);
    }
}
