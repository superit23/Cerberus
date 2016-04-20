package bb.rackmesa.research.authorization;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dan on 4/19/2016.
 */
public class CerbDecryptionMatcher implements CredentialsMatcher {

    private static Logger logger = LoggerFactory.getLogger(CerbDecryptionMatcher.class);

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        CerbAuthRequest request = (CerbAuthRequest)authenticationToken;

        CerbPassInfo passInfo = DatabaseFunctions.getSecret(request.getService(),request.getUser());

        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(256);

        try {
            aesCipherService.decrypt(request.getRequestBody().getBytes(), Base64.decode(passInfo.getToken()));
        }
        catch (CryptoException ex)
        {
            logger.error(ex.getMessage());
            //throw new AuthenticationException();
            return false;
        }

        return true;
    }
}
