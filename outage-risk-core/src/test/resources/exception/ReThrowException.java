package exception;

/**
 * Created by harchen on 2016/7/6.
 */
public class ReThrowException
{
    public void method() throws Exception
    {
        try
        {
            String message = null;
            System.out.println( message.length() );
        }
        catch( Exception e )
        {
            throw e;
        }
    }
}
