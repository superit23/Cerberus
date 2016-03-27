package bb.rackmesa.research.authorization;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.realm.jdbc.JdbcRealm;


/**
 * Created by Dan on 3/21/2016.
 */
public class ServiceMappedDBRealm extends JdbcRealm {

    @Override
    protected org.apache.shiro.authc.AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        CerbAuthToken cToken = (CerbAuthToken)token;

        SimpleAccount accnt = DatabaseFunctions.getUserAuthFromDB(cToken.getService(), (String)cToken.getPrincipal());
        return accnt;

        //return super.doGetAuthenticationInfo(token);
    }

}
