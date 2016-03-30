package bb.rackmesa.research.authorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;

import java.util.Collection;

/**
 * Created by Dan on 3/29/2016.
 */
public class CerbAuthResponse {

    private static Logger logger = LogManager.getLogger(CerbAuthResponse.class);

    private String responseText;
    private String sessionID;
    private Collection<Permission> permissions;

    public String getResponseText()
    {
        return responseText;
    }

    public void setResponseText(String value)
    {
        responseText = value;
    }

    public String getSessionID()
    {
        return sessionID;
    }

    public Collection<Permission> getPermissions()
    {
        return permissions;
    }

    public boolean TryParseResponse(byte[] key)
    {
        try {
            AesCipherService aesCipherService = new AesCipherService();
            aesCipherService.setKeySize(256);
            byte[] decrypted = aesCipherService.decrypt(Base64.decodeToString(responseText).getBytes(), key).getBytes();

            String decryptedAsString = new String(decrypted);


        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());
            return false;
        }


        return true;
    }


}
