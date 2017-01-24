package com.nokia.oss.commons.tools.outage;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by harchen on 2015/12/7.
 */
public class DoNotStartProcessInJava
{
    public String execute( String command )
    {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =new BufferedReader( new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute "+command);
        }
        return output.toString();
    }
}
