package com.nokia.oss.outage.finder;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.nokia.oss.outage.core.OutageRiskFinder;
import com.nokia.oss.outage.core.RootConfig;
import org.apache.commons.cli.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.google.common.base.Preconditions;

/**
 * Created by harchen on 2015/10/12.
 */
public class OutageRiskFinderClient
{
    private static CommandLine commandLine;
    private static String output;
    private static String source;
    private static String format;

    public static void main( String[] args ) throws ParseException
    {
        parseArgs( args );
        System.out.println( "Finding ..." );
        findRisk();
        System.out.println( "Done!" );
    }

    private static void findRisk()
    {
        AbstractApplicationContext context = null;
        try
        {
            context = new AnnotationConfigApplicationContext( RootConfig.class );
            OutageRiskFinder finder = context.getBean( OutageRiskFinder.class );
            List<File> sourcePaths = new ArrayList<File>();
            sourcePaths.add( new File( source ) );
            finder.find( sourcePaths, output, format );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            context.close();
        }
    }

    private static void parseArgs( String[] args ) throws ParseException
    {
        Options options = null;
        try
        {
            options = new Options();
            options.addOption( "source", true, "The source code path" );
            options.addOption( "format", true, "The output format of risk report,default for CSV" );
            options
                .addOption(
                    "output",
                    true,
                    "The output file for outage report, CSV and HTML are supported. Use ; to separate if you want to multi formats. e.g. CSV;HTML" );
            CommandLineParser parser = new DefaultParser();
            commandLine = parser.parse( options, args );
            source = commandLine.getOptionValue( "source" );
            output = commandLine.getOptionValue( "output" );
            format = commandLine.getOptionValue( "format" );
            Preconditions.checkArgument( source != null, "The source code path is null" );
            Preconditions.checkArgument( output != null, "The output path is null" );
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage() );
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar outage-risk-finder.jar", options );
            System.exit( 1 );
        }
    }
}
