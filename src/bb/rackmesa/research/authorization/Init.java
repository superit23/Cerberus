package bb.rackmesa.research.authorization;

import org.apache.logging.log4j.core.util.datetime.DateParser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dan on 3/27/2016.
 */
public class Init {

    public static void main(String[] args)
    {

        ServiceMappedDBRealm realm = new ServiceMappedDBRealm();
        realm.setCredentialsMatcher(new PBKDF2CredentialMatcher());
        CerbSecurityManager secMan = new CerbSecurityManager(realm);

        SecurityUtils.setSecurityManager(secMan);

        Configuration configuration = ((CerbSecurityManager)SecurityUtils.getSecurityManager()).getConfiguration();

        configuration.setApplicationSalt(Base64.decode("8zw19AE3wDmdSwj/Ursy4rXIcbEcPWjX4V79B/JTyZEmf4wWAIQfEWGxOdx3ySXwsQRT3wjxns/QKRWuRLX9oA=="));
        configuration.setPBDKF2Iterations(1024);
        configuration.setPBDKF2NumBytes(32);
        configuration.setDbConnectionString("jdbc:postgresql://cerberus-bb1.cloudapp.net:5432/cerberus?user=cerb&password=cis347");
        configuration.setArtificialWait(100);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Service cerberus = DatabaseFunctions.retrieveService("Cerberus");

        try {
            DatabaseFunctions.createUser(cerberus, "admin", "admin", dateFormat.parse("2020-04-20 00:00:00"));
        }
        catch (ParseException ex)
        {

        }

    }

    public static void Configure()
    {
        ServiceMappedDBRealm realm = new ServiceMappedDBRealm();
        realm.setCredentialsMatcher(new CerbDecryptionMatcher());
        CerbSecurityManager secMan = new CerbSecurityManager(realm);

        SecurityUtils.setSecurityManager(secMan);

        Configuration configuration = ((CerbSecurityManager)SecurityUtils.getSecurityManager()).getConfiguration();

        configuration.setApplicationSalt(Base64.decode("rM+2a3ZBSUg="));
        configuration.setPBDKF2Iterations(4096);
        configuration.setPBDKF2NumBytes(32);
        configuration.setDbConnectionString("jdbc:postgresql://cerberus-bb2.cloudapp.net:5432/cerberus?user=cerb&password=cis347");
        configuration.setArtificialWait(100);
        configuration.setCerberusServer("http://cerberus-bb2.cloudapp.net");
        configuration.setUserSaltLength(4);
        configuration.setServiceSaltLength(4);
    }

    public static Subject EasyAuth(String service, String user, String password)
    {
        CerbServer cerbServer = new CerbServer();
        CerbClient cerbClient = new CerbClient();

        CerbNegotiationResponse negotiationResponse = cerbServer.negotiate(service, user);

        CerbAuthRequest request = new CerbAuthRequest(negotiationResponse, service, user, password);

        CerbAuthResponse response = cerbServer.authenticate(request);
        return cerbClient.processResponse(negotiationResponse, response, password);
    }

}
