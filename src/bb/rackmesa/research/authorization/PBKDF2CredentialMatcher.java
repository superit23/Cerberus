package bb.rackmesa.research.authorization;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dan on 3/18/2016.
 */
public class PBKDF2CredentialMatcher implements CredentialsMatcher {

    private static Logger logger = LoggerFactory.getLogger(PBKDF2CredentialMatcher.class);

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken;
        CerbAccount authInfo = (CerbAccount)authenticationInfo;
        //boolean uNamesMatch = token.getUsername() == authenticationInfo.getPrincipals().fromRealm("Cerberus").iterator().next().toString();
        boolean uNamesMatch = false;
        String derived = null;
        try {
            uNamesMatch = CryptoFunctions.slowEquals(token.getUsername().getBytes(), (authenticationInfo.getPrincipals().fromRealm("Cerberus").iterator().next().toString().getBytes()));
            derived = Base64.encodeToString(CryptoFunctions.pbkdf2(token.getPassword(), authInfo.getSalt(), Configuration.getInstance().getPBDKF2Iterations(), Configuration.getInstance().getPBDKF2NumBytes()));
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());

        }

        boolean passesMatch = CryptoFunctions.slowEquals(derived.getBytes(), ((String)authenticationInfo.getCredentials()).getBytes());
        return passesMatch && uNamesMatch;
    }
}
