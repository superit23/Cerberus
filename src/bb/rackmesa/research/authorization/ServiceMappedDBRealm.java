package bb.rackmesa.research.authorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;


/**
 * Created by Dan on 3/21/2016.
 */
public class ServiceMappedDBRealm extends JdbcRealm {

    private static Logger logger = LogManager.getLogger(ServiceMappedDBRealm.class);

    @Override
    protected org.apache.shiro.authc.AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        CerbAuthToken cToken = (CerbAuthToken)token;

        logger.trace("Sleeping " + Configuration.getInstance().getArtificialWait() + " milliseconds.");
        try
        {
            Thread.sleep(Configuration.getInstance().getArtificialWait());
        }
        catch (InterruptedException ex)
        {
            logger.error(ex.getMessage());
        }

        CerbAccount accnt = DatabaseFunctions.retrieveUser(cToken.getService(), (String)cToken.getPrincipal());
        return accnt;

        //return super.doGetAuthenticationInfo(token);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = principals.fromRealm("Cerberus").iterator().next().toString();
        SimplePrincipalCollection cPrincipals = (SimplePrincipalCollection)principals;
        return DatabaseFunctions.retrieveUser((Service)cPrincipals.fromRealm("Service").iterator().next(), username);

    }

}
