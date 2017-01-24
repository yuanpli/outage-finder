package exception;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by harchen on 2016/9/8.
 */
public class MultiCatchWithoutHanding
{
    private static Object fromXML( File xml )
    {
        Object object = null;
        InputStream inputStream = null;
        Throwable t=null;
        try
        {
            JAXBContext context = JAXBContext.newInstance();
            Unmarshaller unmarshaller = context.createUnmarshaller();
            inputStream = new FileInputStream( xml );
            object = unmarshaller.unmarshal( inputStream );
        }
        catch( FileNotFoundException | JAXBException e )
        {
           t=e;
        }
        return object;
    }
}
