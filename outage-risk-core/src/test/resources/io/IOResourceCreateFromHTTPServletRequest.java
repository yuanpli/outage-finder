package com.nokia.oss.commons.tools.core;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by harchen on 2016/3/25.
 */
public class IOResourceCreateFromHTTPServletRequest
    extends HttpServlet
{
    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        BufferedReader reader = req.getReader();
        String line = null;
        while( (line = reader.readLine()) != null )
        {
            System.out.println( line );
        }
        PrintWriter writer = resp.getWriter();
        writer.println( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" );
        writer.println( "<sessionmanagement>" );
    }

    private void createInvalidationResponse( ServletResponse servletResponse ) throws IOException
    {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        response.setContentType( "text/html;charset=UTF-8" );
        PrintWriter writer = response.getWriter();
        writer.write( "<html><head><title>Illegal application access</title></head><body>" );
        writer.write( "<script type=\"text/javascript\">" );
        writer.write( "alert(\"The current page is no longer valid and will be closed\");" );
        writer.write( "window.close()" );
        writer.write( "</script>" );
        writer.write( "</body></html>" );
        writer.flush();
    }
}
