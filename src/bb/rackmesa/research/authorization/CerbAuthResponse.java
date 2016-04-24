package bb.rackmesa.research.authorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.Collection;

/**
 * Created by Dan on 3/29/2016.
 */
public class CerbAuthResponse {

    private static Logger logger = LogManager.getLogger(CerbAuthResponse.class);

    private String responseText;
    private String encryptedSession;
    private Subject subject;


    public String getResponseText()
    {
        return responseText;
    }

    public void setResponseText(String value)
    {
        responseText = value;
    }


    public String getEncryptedSession()
    {
        return encryptedSession;
    }

    public Subject getSubject()
    {
        return subject;
    }



    public CerbAuthResponse(String sessionID, String sessionKey, byte[] key, String responseText, Subject subject)
    {
        Configuration configuration = ((CerbSecurityManager) SecurityUtils.getSecurityManager()).getConfiguration();

        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(configuration.getPBDKF2NumBytes() * 8);

        encryptedSession =  aesCipherService.encrypt((sessionID + ":" + sessionKey + ":" + System.currentTimeMillis()).getBytes(), key).toBase64();

        this.encryptedSession = encryptedSession;
        this.responseText = responseText;
        this.subject = subject;
    }

    public CerbAuthResponse(String responseText)
    {
        this.responseText = responseText;
    }


}
