package com.nokia.oss.outage.core.report;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nokia.oss.outage.core.bean.OutageRiskList;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * Created by harchen on 2016/3/29.
 */
public class OutageRiskHTMLReporter
    implements OutageRiskReporter
{
    private static final Logger LOG = LoggerFactory.getLogger( OutageRiskHTMLReporter.class );

    @Override
    public void report( Writer writer, OutageRiskList risks )
    {
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_23 );
        cfg.setClassForTemplateLoading( this.getClass(), "/" );
        cfg.setDefaultEncoding( "UTF-8" );
        cfg.setTemplateExceptionHandler( TemplateExceptionHandler.RETHROW_HANDLER );
        cfg.setLogTemplateExceptions( false );
        Map root = new HashMap();
        root.put( "risks", risks.getRisks() );
        try
        {
            Template temp = cfg.getTemplate("template/outage_risk_template.ftl");
            temp.process( root, writer );
        }
        catch( Exception e )
        {
            LOG.error( "Failed to write the result to HTML", e );
        }
    }
}
