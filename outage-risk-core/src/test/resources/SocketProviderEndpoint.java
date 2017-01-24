import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.servicemix.common.endpoints.ProviderEndpoint;


/**
 * @org.apache.xbean.XBean element="provider"
 */
public class SocketProviderEndpoint
    extends ProviderEndpoint
    implements SocketEndpointType
{
    private Socket socket;
    private PrintWriter pw;
    private String ip = "";
    private int port = 0;
    private String hostId = "Unknown";
    private Date startTime = new Date( 0 );
    private Date endTime = null;
    private Timer timer = null;

    public static final String BUFPAD = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";


    protected synchronized void checkConnection()
    {
        try
        {
            Thread.currentThread().setName( "Thread-host-" + this.getHostId() );
            boolean enable = Config.getBoolean( "com.nsn.oss.nbi.fmascii." + this.getHostId() + ".enable", true );
            if( !enable )
            {
                if( socket != null )
                {
                    prevContent = "";
                    this.closeSocket();
                    LOGGER.info( "this host had been disabled, close socket" );
                }
                return;
            }
            checkDestChange();
            endTime = new Date();
            long span = endTime.getTime() - startTime.getTime();

            long period = Long.valueOf( Config.getLong( "com.nsn.oss.nbi.fmascii.nms.check.period", 120 ) );
            if( span < (period * 1000) )
            {
                // LOGGER.trace("too short to check NMS, skip:"+span);
                return;
            }
            LOGGER.info( "Check NMS status,host is " + getHostId() );
            startTime = endTime;
            if( socket == null )
            {
                createSocket( ip, port );
            }
            else
            {
                sendHeartBeat();
            }
            clearSMAlarm();
        }
        catch( IOException e )
        {
            LOGGER.info( "NMS disconnect,send alarm to self-monitor,host is " + getHostId(), e );
            sendSMAlarm();
            closeSocket();
        }
        catch( NumberFormatException e )
        {
            LOGGER.info( "invalid config:" + getHostId(), e );
            sendSMAlarm();
            closeSocket();
        }
    }


    protected void sendHeartBeat() throws IOException
    {
        boolean heartBeat = Config.getBoolean( Definitions.ISSENDHEARTBEAT, true );
        if( heartBeat )
        {
            trySendContent( BUFPAD );
        }
    }


    private synchronized void startConnectionCheckingTask()
    {
        timer = new Timer( "ASCII_SOCKET_TIMER" + "-" + this.getHostId() );
        timer.schedule( new TimerTask()
        {
            @Override
            public void run()
            {
                checkConnection();
            }
        }, 500, 5 * 1000 );
        LOGGER.info( "start nms check timer,host is " + this.getHostId() + ",ip: " + getIp() + ",port: " + getPort() );
    }


    @Override
    public void activate()
    {
        LOGGER.info( "activate SocketProviderEndpoint" );
        try
        {
            super.activate();
            startConnectionCheckingTask();
        }
        catch( Exception e1 )
        {
            // TODO Auto-generated catch block
            LOGGER.error( "activate failed", e1 );
        }
    }


    public synchronized void cancelTimer()
    {
        timer.cancel();
    }


    @Override
    public void deactivate()
    {
        LOGGER.info( "deactivate SocketProviderEndpoint" );
        try
        {
            super.deactivate();
            cancelTimer();
        }
        catch( Exception e )
        {
            // TODO Auto-generated catch block
            LOGGER.error( "deactivate failed" );
        }
    }


    protected synchronized void createSocket( String ip, int port ) throws UnknownHostException, IOException
    {
        LOGGER.trace( "ip: " + ip + ", port: " + port );
        socket = new Socket( ip, port );
        socket.setTcpNoDelay( true );
        pw = new PrintWriter( socket.getOutputStream(), true );
    }


    protected void closeSocket()
    {
        if( socket != null )
        {
            try
            {
                socket.close();
            }
            catch( IOException ex )
            {
                LOGGER.error( ex );
            }
            finally
            {
                socket = null;
                pw = null;
            }
        }
    }
}
