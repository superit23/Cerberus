package bb.rackmesa.research.authorization;

import org.apache.logging.log4j.core.util.datetime.DateParser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSecurityManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dan on 3/27/2016.
 */
public class Init {

    public static void main(String[] args)
    {
        Configuration.getInstance().setApplicationSalt(Base64.decode("8zw19AE3wDmdSwj/Ursy4rXIcbEcPWjX4V79B/JTyZEmf4wWAIQfEWGxOdx3ySXwsQRT3wjxns/QKRWuRLX9oA=="));
        Configuration.getInstance().setPBDKF2Iterations(1024);
        Configuration.getInstance().setPBDKF2NumBytes(256);
        Configuration.getInstance().setDbConnectionString("jdbc:postgresql://cerberus-bb1.cloudapp.net:5432/cerberus?user=cerb&password=cis347");
        Configuration.getInstance().setArtificialWait(100);

        DefaultSecurityManager secMan = new DefaultSecurityManager();

        ServiceMappedDBRealm realm = new ServiceMappedDBRealm();
        realm.setCredentialsMatcher(new PBKDF2CredentialMatcher());
        secMan.setRealm(realm);

        SecurityUtils.setSecurityManager(secMan);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Service cerberus = DatabaseFunctions.retrieveService("Cerberus");

        try {
            DatabaseFunctions.createUser(cerberus, "admin", "admin", dateFormat.parse("2020-04-20 00:00:00"));
        }
        catch (ParseException ex)
        {

        }

    }

}
