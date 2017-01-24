import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.spi.Provider;

import com.nokia.oss.interfaces.licensing.ws.client.LicenseCheckWebService;
import com.nokia.oss.interfaces.licensing.ws.client.LicenseCheckWs;

public class LicProxyImpl implements Runnable, LicProxy {

    static Runtime runtime = Runtime.getRuntime();
    private final static String DEFAULT_USER = "omc";

    private static LicenseCheckWs lkCheck = null;
    private ServiceInfoCallBack serviceInfo = null;

    public LicProxyImpl(final long update_period, final String wasAdress,final String userName){
        this.serviceInfo = new ServiceInfoCallBack(){
            @Override
            public String getPassWord() {
                return getPassWordDefault(getServiceName());
            }

            @Override
            public String getServiceName() {
                return userName;
            }
        };
    }
    public LicProxyImpl(long update_period, String wasAdress) {
        this(update_period,wasAdress,DEFAULT_USER);
    }

    public static String getPassWordDefault(String userName) {
        BufferedReader br = null;
        String password = null;
        try {
            String command = "/opt/nokia/oss/bin/syscredacc.sh -user " + userName + " -type appserv -instance appserv";
            Process p = runtime.exec(command);
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            br = new BufferedReader(new InputStreamReader(in));
            password = br.readLine();
            LOGGER.trace("license userName="+userName+",password=" +password);
        } catch (IOException e) {
            LOGGER.error("getPassWord failed", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.error("close IO failed", e);
                }
            }
        }
        return password;
    }


    protected synchronized LicenseCheckWs getLicenseCheckPortGlobal(String wasAdress) throws
            MalformedURLException {

        System.setProperty(Provider.JAXWSPROVIDER_PROPERTY, DEFAULT_JAXWSPROVIDER);
        String serviceUrl = "http://" + wasAdress + "/licensing/ws/LicenseCheckWebService/LicenseCheckWebService.wsdl";
        LicenseCheckWebService lkService;
        lkService = new LicenseCheckWebService(new URL(serviceUrl),
                new QName("http://www.nsn.com/schemas/public/oss/interfaces/ws-api/services/licensing",
                        "LicenseCheckWebService"));
        lkCheck = lkService.getLicenseCheckWebServicePort();
        Map<String, Object> requestContext = ((BindingProvider) lkCheck).getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, serviceInfo.getServiceName());
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, serviceInfo.getPassWord());
        LOGGER.info("ProviderImpl:javax.xml.ws.spi.Provider=" + Provider.provider().toString());
        return lkCheck;
    }
}
