package bb.rackmesa.research.authorization;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.AesCipherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Dan on 3/29/2016.
 */
public class CerbAuthRequest extends UsernamePasswordToken {

    private static Logger logger = LoggerFactory.getLogger(PBKDF2CredentialMatcher.class);

    private String service;
    private String user;
    private String requestBody;
    private byte[] salt;


    public void setSalt(byte[] value) { salt = value; }

    public byte[] getSalt() { return salt; }

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

    public CerbAuthRequest(String service, String user, String password)
    {
        setService(service);
        setUser(user);
        setSalt(CryptoFunctions.generateSalt(128));

        Configuration configuration = ((CerbSecurityManager) SecurityUtils.getSecurityManager()).getConfiguration();

        try {
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

        requestBody = aesCipherService.encrypt((service + ":" + user + ":" + System.currentTimeMillis()).getBytes(), key).toBase64();

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
