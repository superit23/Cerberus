package bb.rackmesa.research.authorization;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;


/**
 * Created by Dan on 3/18/2016.
 */
public class ShiroFunctions {


    public ShiroFunctions()
    {
        SecurityUtils.setSecurityManager(secMan);

    }

    public static JdbcRealm realm = new JdbcRealm();

    public static SecurityManager secMan = new DefaultSecurityManager();

    public static Subject getSubject(String service, String username)
    {
        Subject user = new Subject.Builder().buildSubject();

        JndiLdapRealm tRealm = new JndiLdapRealm();



        return user;

    }
}
