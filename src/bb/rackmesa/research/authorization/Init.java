package bb.rackmesa.research.authorization;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSecurityManager;

/**
 * Created by Dan on 3/27/2016.
 */
public class Init {

    public static void main(String[] args)
    {
        Configuration.getInstance().setApplicationSalt(Base64.decode("bGAmMZEOUwn+pIvUw2w31kIMnRM="));
        Configuration.getInstance().setPBDKF2Iterations(1024);
        Configuration.getInstance().setPBDKF2NumBytes(150);
        Configuration.getInstance().setDbConnectionString("jdbc:postgresql://cerberus-bb1.cloudapp.net:5432/cerberus?user=cerb&password=cis347");
        Configuration.getInstance().setArtificialWait(100);

        DefaultSecurityManager secMan = new DefaultSecurityManager();

        ServiceMappedDBRealm realm = new ServiceMappedDBRealm();
        realm.setCredentialsMatcher(new PBKDF2CredentialMatcher());
        secMan.setRealm(realm);

        SecurityUtils.setSecurityManager(secMan);

    }

}
