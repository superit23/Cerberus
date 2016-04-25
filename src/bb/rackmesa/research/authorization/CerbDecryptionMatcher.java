package bb.rackmesa.research.authorization;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dan on 4/19/2016.
 */
public class CerbDecryptionMatcher implements CredentialsMatcher {

    private static Logger logger = LoggerFactory.getLogger(CerbDecryptionMatcher.class);

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        CerbAuthRequest request = (CerbAuthRequest)authenticationToken;
        CerbAccount account = (CerbAccount)authenticationInfo;


        //CerbPassInfo passInfo = DatabaseFunctions.getSecret(request.getService(),request.getUser());

        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(((CerbSecurityManager)SecurityUtils.getSecurityManager()).getConfiguration().getPBDKF2NumBytes() * 8);

        boolean uNamesMatch = false;

        try {
            //logger.info(account.getCredentials().toString());
            String reqInfo = Base64.decodeToString(aesCipherService.decrypt(Base64.decode(request.getRequestBody()), Base64.decode(account.getCredentials().toString())).toString());
            String[] reqSplit = reqInfo.split(":");

            long currentTime = new Date().toInstant().getEpochSecond();
            long requestAge =  currentTime - Long.parseLong(reqSplit[2]);

            if (!(requestAge > 0 && requestAge < 20)) {
                logger.warn("Decryption successful, but request expired for " + request.getUser());
                return false;
            }

            uNamesMatch = request.getUser() == account.getPrincipals().fromRealm("Cerberus").iterator().next().toString();
        } catch (CryptoException ex) {
            logger.error(ex.getMessage());
            //throw new AuthenticationException();
            return false;
        }

        return uNamesMatch;
    }
}
