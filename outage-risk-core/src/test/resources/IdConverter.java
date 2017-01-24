/**
 * Created by harchen on 8/27/2015.
 */
public class IdConverter
{

    public String moidToDn( MOID moid, String systemId ) throws NWI3ApplicationException
    {
        NWI3MOID.validate( moid );
        if( systemId == null || systemId.isEmpty() )
        {
            throw new IllegalArgumentException( "SystemId cannot be null!" );
        }

        final String rdn = moidToRdn( moid );
        try
        {
            Nwi3InterfaceDto agentInterface = nasdaAccess.getNwi3InterfaceBySystemId( systemId );
            String agentFqdn = agentInterface.getAgentFqdn();
            String agentParentDn = extractParent( agentFqdn );
            return agentParentDn + "/" + rdn;

        }
        catch( NasdaAccessException e )
        {
            // TODO Auto-generated catch block
            LOG.warn(
                String.format(
                    "failed to get the MOID for agent with systemId %s from NASDA [%s];", systemId, e.getMessage() ),
                e );
            throw new NWI3ApplicationException(
                "MOID to FQDN conversion failed! systemId did not match to any object [MOID.BaseId=" + moid.BaseId +
                    "]" );
        }
    }

}
