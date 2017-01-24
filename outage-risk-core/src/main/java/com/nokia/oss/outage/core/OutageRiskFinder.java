package com.nokia.oss.outage.core;

import com.nokia.oss.outage.core.bean.OutageRiskList;
import com.nokia.oss.outage.core.bean.RiskLevel;
import com.nokia.oss.outage.core.report.OutageRiskCSVReporter;
import com.nokia.oss.outage.core.report.OutageRiskHTMLReporter;
import com.nokia.oss.outage.core.report.OutageRiskReporter;
import com.nokia.oss.outage.core.report.OutageRiskXMLReporter;
import com.nokia.oss.outage.core.rule.OutageRiskRule;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by harchen on 2015/10/12.
 */
@Component
public class OutageRiskFinder
{
    private static final Logger LOG = LoggerFactory.getLogger( OutageRiskFinder.class );
    @Autowired
    private List<OutageRiskRule> rules = new ArrayList<OutageRiskRule>();

    public void find( List<File> sourcePaths, String output, String format ) throws IOException
    {
        OutageRiskList risks = find( sourcePaths );
        risks.sort();
        report( risks, output, format );
    }

    public OutageRiskList find( List<File> sourcePaths ) throws IOException
    {
        List<File> sourceFiles = getSourceFiles( sourcePaths );
        OutageRiskList outageRisks = findRisks( sourceFiles );
        statistic( outageRisks );
        return outageRisks;
    }

    private List<File> getSourceFiles( List<File> sourcePaths ) throws IOException
    {
        LOG.info( "Search for paths " + sourcePaths );
        List<File> sourceFiles = new ArrayList<File>();
        for( File path : sourcePaths )
        {
            if( path.exists() && path.isDirectory() && path.canRead() )
            {
                sourceFiles.addAll( listSourceFile( path ) );
            }
        }
        LOG.info( sourceFiles.size() + " source file found!" );
        return sourceFiles;
    }

    private void statistic( OutageRiskList outageRisks )
    {
        int blocker = outageRisks.countByLevel( RiskLevel.BLOCKER );
        int critical = outageRisks.countByLevel( RiskLevel.CRITICAL );
        int major = outageRisks.countByLevel( RiskLevel.MAJOR );
        int minor = outageRisks.countByLevel( RiskLevel.MINOR );
        int info = outageRisks.countByLevel( RiskLevel.INFO );

        LOG.info(
            "Outage risks statistic: {} blocker, {} critical, {} major, {} minor, {} info", blocker, critical, major,
            minor, info );

    }

    private OutageRiskList findRisks( List<File> sourceFiles )
    {
        OutageRiskList risks = new OutageRiskList();
        int percentage = 0;
        for( int i = 0; i < sourceFiles.size(); i++ )
        {
            File source = sourceFiles.get( i );
            for( OutageRiskRule rule : rules )
            {
                risks.addAll( rule.check( source ) );
            }
            if( percentage != getPercentage( sourceFiles.size(), i + 1 ) )
            {
                percentage = getPercentage( sourceFiles.size(), i + 1 );
                LOG.info( "Outage risk scanning:" + percentage + "% finished " );
            }
        }
        return risks;
    }

    private int getPercentage( int total, int value )
    {
        return (int)(((double)value / total) * 100);
    }

    private List<File> listSourceFile( File root ) throws IOException
    {
        List<File> fileList = new LinkedList<File>();
        Collection<File> files = FileUtils.listFiles( root, new String[] { "java" }, true );
        String testFile = File.separator + "src" + File.separator + "test" + File.separator;
        for( File file : files )
        {
            if( !file.getAbsolutePath().contains( testFile ) )
            {
                fileList.add( file );
            }
        }
        return fileList;
    }

    private static void report( OutageRiskList risks, String output, String format ) throws IOException
    {
        Writer writer = null;
        try
        {
            if( format == null )
            {
                format = "CSV";
            }
            String[] formats = format.split( ";" );
            for( String outputFormat : formats )
            {
                OutageRiskReporter reporter = null;
                File outputFile = null;
                if( "CSV".equalsIgnoreCase( outputFormat ) )
                {
                    reporter = new OutageRiskCSVReporter();
                    outputFile = new File( output + ".csv" );

                }
                else if( "HTML".equalsIgnoreCase( outputFormat ) )
                {
                    reporter = new OutageRiskHTMLReporter();
                    outputFile = new File( output + ".html" );
                }
                else if( "XML".equalsIgnoreCase( outputFormat ) )
                {
                    reporter = new OutageRiskXMLReporter();
                    outputFile = new File( output + ".xml" );
                }
                else
                {
                    System.out.println( "Unsupported output format " + outputFormat );
                    continue;
                }
                System.out.println( "Write report to " + outputFile );
                writer = new FileWriter( outputFile );
                reporter.report( writer, risks );
            }
        }
        finally
        {
            IOUtils.closeQuietly( writer );
        }
    }

    public void setRules( List<OutageRiskRule> rules )
    {
        this.rules = rules;
    }
}
