package com.nokia.oss.outage.core.report;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.nokia.oss.outage.core.bean.OutageRisk;
import com.nokia.oss.outage.core.bean.OutageRiskList;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by harchen on 2015/10/12.
 */
public class OutageRiskCSVReporter
    implements OutageRiskReporter
{
    @Override
    public void report( Writer writer, OutageRiskList risks )
    {
        FileWriter fileWriter = null;
        CSVWriter csvWriter = null;
        try
        {
            csvWriter = new CSVWriter( writer, ',' );
            writeHeader( csvWriter );
            writeRows( csvWriter, risks.getRisks() );
        }
        finally
        {
            IOUtils.closeQuietly( csvWriter );
            IOUtils.closeQuietly( fileWriter );
        }
    }

    private void writeHeader( CSVWriter writer )
    {
        writer.writeNext( new String[] { "Id", "ClassName", "Line", "Type", "Level", "Description", "Sample","Path" } );
    }

    private void writeRows( CSVWriter writer, List<OutageRisk> risks )
    {
        for( int i = 0; i < risks.size(); i++ )
        {
            OutageRisk risk = risks.get( i );
            ArrayList<String> fields = new ArrayList();
            fields.add( String.valueOf( i + 1 ) );
            fields.add( risk.getClassName() );
            fields.add( String.valueOf( risk.getRow() ) );
            fields.add( risk.getType().getId() );
            fields.add( risk.getType().getRiskLevel().name() );
            fields.add( risk.getType().getDescription() );
            fields.add( risk.getSample() );
            fields.add( risk.getPath() );
            writer.writeNext( fields.toArray( new String[fields.size()] ) );
        }
    }

}
