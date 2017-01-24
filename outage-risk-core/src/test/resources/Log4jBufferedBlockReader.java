/**
 * Created by harchen on 8/27/2015.
 */
public class Log4jBufferedBlockReader
{
    public void close()
    {
        try
        {
            line = null;
            bReader.close();
        }
        catch( IOException e )
        {

        }
    }
}
