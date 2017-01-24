/**
 * Created by harchen on 8/27/2015.
 */
public class DBAccessor implements MessageExchangeListener {
    @Resource
    private DeliveryChannel channel;

    @Override
    public void onMessageExchange( MessageExchange exchange ) throws MessagingException {

        Source messageContent = JDBCMessages.marshallJDBCQueryMessage(new JDBCMessages.SQLQuery[] { new JDBCMessages.SQLQuery(SQLQueryType.SELECT,
                query,
                parameters) });

        InOut jdbcMEX = sendInOutMessage(messageContent);

        Fault fault = jdbcMEX.getFault();

        if( fault != null )
        {
            done(jdbcMEX);

            throw new MessagingException("JDBC BC sent fault");
        }
        Source content = getOutMessageContent(jdbcMEX);

        done(jdbcMEX);

        return content;
    }


    protected InOut sendInOutMessage( Source messageContent ) throws MessagingException
    {
        InOut mex = createInOut();
        initializeMexWithInMessage(messageContent, mex);

        sendSync(mex);
        return mex;
    }

    protected void done( MessageExchange mex ) throws MessagingException
    {
        mex.setStatus(ExchangeStatus.DONE);
        send(mex);
    }
}
