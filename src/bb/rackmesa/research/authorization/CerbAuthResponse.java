package bb.rackmesa.research.authorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.session.Session;

import java.util.Collection;

/**
 * Created by Dan on 3/29/2016.
 */
public class CerbAuthResponse {

    private static Logger logger = LogManager.getLogger(CerbAuthResponse.class);

    private String responseText;
    private int sessionID;
    private String sessionKey;
    private CerbAccount account;

    public String getResponseText()
    {
        return responseText;
    }

    public void setResponseText(String value)
    {
        responseText = value;
    }

    public int getSessionID()
    {
        return sessionID;
    }

    public String getSessionKey()
    {
        return sessionKey;
    }

    public CerbAccount getUser()
    {
        return account;
    }


    public CerbAuthResponse(int sessionID, String sessionKey, String responseText, CerbAccount account)
    {
        this.sessionID = sessionID;
        this.sessionKey = sessionKey;
        this.responseText = responseText;
        this.account = account;
    }


    public CerbAuthResponse(String encryptedSession, byte[] key, String responseText, CerbAccount account)
    {
        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(256);


        String[] sessionInfo = aesCipherService.decrypt(Base64.decode(encryptedSession), key).toString().split(":");

        int sessionID = Integer.parseInt(sessionInfo[0]);
        String sessionKey = sessionInfo[1];


        this.sessionID = sessionID;
        this.sessionKey = sessionKey;
        this.responseText = responseText;
        this.account = account;
    }


}
