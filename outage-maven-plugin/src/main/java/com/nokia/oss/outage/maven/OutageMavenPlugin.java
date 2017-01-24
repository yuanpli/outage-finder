package com.nokia.oss.outage.maven;

import com.nokia.oss.outage.core.OutageRiskFinder;
import com.nokia.oss.outage.core.RootConfig;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by harchen on 2015/12/21.
 */
@Mojo( name = "outage" )
public class OutageMavenPlugin
    extends AbstractMojo
{
    private static final String DATE_FORMAT = "yyyyMMddHHmmsss";

    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;
    @Parameter( property = "project.build.directory", required = true )
    private File targetDirectory;
    @Parameter( property = "aggregate", defaultValue = "true" )
    protected boolean aggregate;
    @Parameter( property = "outputFormat", defaultValue = "XML" )
    private String outputFormat;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if( aggregate && !project.isExecutionRoot() )
        {
            return;
        }
        executeCheck();

    }

    private void executeCheck()
    {
        AbstractApplicationContext context = null;
        try
        {
            context = new AnnotationConfigApplicationContext( RootConfig.class );
            OutageRiskFinder finder = context.getBean( OutageRiskFinder.class );
            List<File> sourceDirectories = new ArrayList<File>();
            sourceDirectories.add( project.getBasedir() );
            String output = getOutputPath();
            finder.find( sourceDirectories, output, outputFormat );
        }
        catch( Exception e )
        {
            getLog().error( "Failed to find outage risk", e );
        }
        finally
        {
            context.close();
        }
    }

    private String getOutputPath()
    {
        File outage = new File( targetDirectory, "outage-risks" );
        File current = new File( outage, new SimpleDateFormat( DATE_FORMAT ).format( new Date() ) );
        if( !current.mkdirs() )
        {
            throw new IllegalStateException( "Can not create directory under " + outage.getAbsolutePath() );
        }
        return new File( current, project.getArtifactId() + "_outage_risk" ).getAbsolutePath();
    }

    public void setProject( MavenProject project )
    {
        this.project = project;
    }

    public void setOutputFormat( String outputFormat )
    {
        this.outputFormat = outputFormat;
    }
}
