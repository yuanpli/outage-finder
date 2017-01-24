package com.nokia.oss.outage.core.rule;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.nokia.oss.outage.core.bean.OutageRisk;

/**
 * Created by harchen on 2015/10/21.
 */
public abstract class AbstractOutageRiskRule
    implements OutageRiskRule
{
    private static final Logger LOG = LoggerFactory.getLogger( AbstractOutageRiskRule.class );

    @Override
    public List<OutageRisk> check(File source )
    {
        LOG.debug( "Check file " + source.getAbsoluteFile() );
        List<OutageRisk> risks = new ArrayList<OutageRisk>();
        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream( source );
            CompilationUnit cu = JavaParser.parse( inputStream );
            List<OutageRisk> found = checkForRisks( cu );
            LOG.debug( "Risk found:" + found.size() );
            for( OutageRisk risk : found )
            {
                risk.setPath( source.getAbsolutePath() );
                risks.add( risk );
            }

        }
        catch( Exception e )
        {
            LOG.error( "Failed to check file " + source.getAbsoluteFile(), e );
        }
        finally
        {
            IOUtils.closeQuietly( inputStream );
        }

        return risks;
    }

    protected abstract List<OutageRisk> checkForRisks( CompilationUnit cu );
}
