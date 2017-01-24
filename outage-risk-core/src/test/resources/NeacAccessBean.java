/**
 * Created by harchen on 8/26/2015.
 */
public class NeacAccessBean
    implements MessageExchangeListener
{
    private static final XoHLogger LOGGER = XoHLogger.getLogger( NeacAccessBean.class );
    @Resource
    private DeliveryChannel channel;
    @Resource
    private ComponentContext context;


    @Override
    public void onMessageExchange( MessageExchange exchange ) throws MessagingException
    {
        LOGGER.entering( "onMessageExchange", exchange );
        if( exchange.getStatus() == ExchangeStatus.DONE || exchange.getStatus() == ExchangeStatus.ERROR )
        {
            return;
        }
        InOut inOut = null;

        inOut = channel.createExchangeFactory().createInOutExchange();
        QName neacService = new QName( "http://xoh.com/common/1.0", "CredentialEnricher" );
        inOut.setService( neacService );
        inOut.setEndpoint( context.getEndpoint( neacService, "CredentialEnricher" ) );
        inOut.setInMessage( exchange.getMessage( "in" ) );

        for( Object propertyName : exchange.getPropertyNames() )
        {
            inOut.setProperty( (String)propertyName, exchange.getProperty( (String)propertyName ) );
        }
        QName service = inOut.getEndpoint().getServiceName();
        String endpoint = inOut.getEndpoint().getEndpointName();
        LOGGER.debug( "Sending message to {}:{}", service, endpoint );
        channel.sendSync( inOut );
        LOGGER.debug( "Receive response from {}:{}", service, endpoint );

        LOGGER.exiting( "onMessageExchange" );
    }

}
