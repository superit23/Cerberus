package bb.rackmesa.research.authorization;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

/**
 * Created by Dan on 3/29/2016.
 */
public class CerbAuthRequest extends UsernamePasswordToken {

    private static Logger logger = LoggerFactory.getLogger(CerbAuthRequest.class);

    private String service;
    private String user;
    private String requestBody;

    public String getService()
    {
        return service;
    }

    public void setService(String value)
    {
        service = value;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String value)
    {
        user = value;
    }

    //public byte[] getKey()
    //{
    //    return key;
    //}

    public CerbAuthRequest(CerbNegotiationResponse negotiationResponse, String service, String user, String password)
    {
        setService(service);
        setUser(user);

        Configuration configuration = ((CerbSecurityManager) SecurityUtils.getSecurityManager()).getConfiguration();
        byte[] salt = CryptoFunctions.combineArrays(negotiationResponse.getConfiguration().getApplicationSalt(), CryptoFunctions.combineArrays(negotiationResponse.getServiceSalt(), negotiationResponse.getUserSalt()));

        try {
            //logger.info(Base64.encodeToString(CryptoFunctions.pbkdf2(password.toCharArray(), salt, configuration.getPBDKF2Iterations(), configuration.getPBDKF2NumBytes())));
            setRequestBody(CryptoFunctions.pbkdf2(password.toCharArray(), salt, configuration.getPBDKF2Iterations(), configuration.getPBDKF2NumBytes()));
        }
        catch (NoSuchAlgorithmException ex)
        {
            logger.error(ex.getMessage());
        }
        catch (InvalidKeySpecException ex)
        {
            logger.error(ex.getMessage());
        }
    }

    public void setRequestBody(byte[] key)
    {
        Configuration configuration = ((CerbSecurityManager) SecurityUtils.getSecurityManager()).getConfiguration();

        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(configuration.getPBDKF2NumBytes() * 8);
        //requestBody =  "AUTH_REQUEST:" + service + ":" + user + ":" + aesCipherService.encrypt((service + ":" + user + ":" + System.currentTimeMillis()).getBytes(), key).toBase64();

        requestBody = aesCipherService.encrypt((service + ":" + user + ":" + new Date().toInstant().getEpochSecond()).getBytes(), key).toBase64();

    }

    public String getRequestBody()
    {
        return requestBody;
    }


    @Override
    public Object getPrincipal() {
        return getUser();
    }

    @Override
    public Object getCredentials() {
        return requestBody;
    }
}
