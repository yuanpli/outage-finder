package memory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Created by harchen on 2016/9/14.
 */
public class CreateJaxbContextInMethodAndAssignToLocalVariable
{
    public String toXML( Object object ) throws JAXBException
    {
        JAXBContext context = JAXBContext.newInstance( object.getClass() );
        Marshaller marshaller = context.createMarshaller();
        StringWriter content = new StringWriter();
        marshaller.marshal( object, content );
        return content.toString();
    }
}
