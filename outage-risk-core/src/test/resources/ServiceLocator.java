import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.nsn.credentialaccessapi.SystemCredentialAccess;
import com.nsn.credentialaccessapi.SystemCredentialAccessInterface;
import com.nsn.obs.mw.mf.core.config.Config;
import com.nsn.oss.nasda.objectregistry.registryservice.ObjectRegistryServiceHome;
import com.nsn.oss.nasda.objectregistry.registryservice.ObjectRegistryServiceRemote;
import com.nsn.oss.nasda.relationshipservice.irp.RelationshipService;

public class ServiceLocator {
    private static final Logger LOGGER = Logger.getLogger(ServiceLocator.class);

    private synchronized static Context getContext() throws NamingException, IOException {
        return new InitialContext(prepareProperties());
    }

    public synchronized static RelationshipService getRelationshipService() throws NamingException, IOException {
        return (RelationshipService) getContext().lookup(RelationshipService.class.getName());

    }

    public synchronized static ObjectRegistryServiceRemote getRegistryService() throws NamingException, CreateException, IOException {
        LOGGER.debug("ObjectRegistryServiceHome.JNDI_NAME" + ObjectRegistryServiceHome.JNDI_NAME);
        ObjectRegistryServiceHome home = (ObjectRegistryServiceHome) getContext().lookup(ObjectRegistryServiceHome.JNDI_NAME);
        return home.create();

    }

    private static InputStream getPropertyInputStream() throws FileNotFoundException {
        String wasProp = Config.get("com.nsn.obs.mf.mediations.oes.was.ejb.access.properties", "d:\\mf-oes-platform.properties");
        LOGGER.debug("Using property file @ " + wasProp + " for SCA");
        return new FileInputStream(wasProp);
    }

    private static String getLoggedinUserPassword(String user, String type) {
        SystemCredentialAccessInterface sysCredAccInt = new SystemCredentialAccess();
        return sysCredAccInt.getPassword(user, type, type);
    }

    private static Properties prepareProperties() throws IOException {
        String user = Config.get("com.ibm.CORBA.loginUserid", "wassrvid");
        LOGGER.debug("User name from config:" + user);
        String password = "wassrvid";
        final String type = "APPSERV";
        password = getLoggedinUserPassword(user, type);
        if (null == password) {
            password = "wassrvid";
        }
        LOGGER.debug( "User password:" + password);
        Properties prop = new Properties();
        prop.load(getPropertyInputStream());
        prop.put("com.ibm.CORBA.loginUserid", user);
        prop.put("com.ibm.CORBA.loginPassword", password);
        prop.put(Context.SECURITY_PRINCIPAL, user);
        prop.put(Context.SECURITY_CREDENTIALS, password);
        prop.put("com.ibm.CORBA.loginUserid", user);
        prop.put("com.ibm.CORBA.loginPassword", password);

        return prop;
    }

}
