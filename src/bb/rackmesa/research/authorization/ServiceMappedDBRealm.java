package bb.rackmesa.research.authorization;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;

/**
 * Created by Dan on 3/21/2016.
 */
public class ServiceMappedDBRealm extends JdbcRealm {

    @Override
    protected org.apache.shiro.authc.AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return super.doGetAuthenticationInfo(token);
    }

}
