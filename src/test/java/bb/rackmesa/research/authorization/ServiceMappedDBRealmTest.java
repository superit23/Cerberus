package bb.rackmesa.research.authorization;

import bb.rackmesa.research.authorization.CerbAuthToken;
import bb.rackmesa.research.authorization.Init;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dan on 3/27/2016.
 */
public class ServiceMappedDBRealmTest {

    @Test
    public void doGetAuthenticationInfo() throws Exception {
        Init.Configure();

        Subject cUser = SecurityUtils.getSubject();
        cUser.login(new CerbAuthToken("Test Service", "dcronce", "3ab3054b-3944-44e4-8bfb-4663495f9f1a"));
        assertTrue(cUser.isAuthenticated());
    }
}