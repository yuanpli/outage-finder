package com.nokia.oss.outage.core.report;

import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nokia.oss.outage.core.bean.OutageRiskList;

/**
 * Created by harchen on 2016/6/2.
 */
public class OutageRiskXMLReporter
    implements OutageRiskReporter
{
    private static final Logger LOG = LoggerFactory.getLogger( OutageRiskXMLReporter.class );

    @Override
    public void report( Writer writer, OutageRiskList risks )
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance( OutageRiskList.class );
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            marshaller.marshal( risks, writer );
        }
        catch( JAXBException e )
        {
            LOG.error( "Fail to write outage risks to XML", e );
        }

    }
}
