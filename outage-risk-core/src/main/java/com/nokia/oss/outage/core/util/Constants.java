package com.nokia.oss.outage.core.util;

/**
 * Created by harchen on 2015/10/13.
 */
public class Constants
{
    public static final Class[] IO_TYPES = { java.io.InputStream.class, java.io.OutputStream.class,
                                            java.io.Reader.class, java.io.Writer.class };
    public static final Class[] IGNORED_IO_TYPES = { java.io.ByteArrayOutputStream.class,
                                                    java.io.ByteArrayInputStream.class, java.io.StringReader.class,
                                                    java.io.StringWriter.class, java.io.CharArrayReader.class,
                                                    java.io.CharArrayWriter.class };
    public static final String IO_CLOSE_METHOD = "close";
    public static final String[] IO_CLOSE_UTILITY_METHOD = { "IOUtils.closeQuietly" };
}
