package bb.rackmesa.research.authorization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.subject.Subject;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


/**
 * Created by Dan on 3/29/2016.
 */
public class CerbClient {

    private static Logger logger = LogManager.getLogger(CerbAuthResponse.class);

    Configuration configuration = ((CerbSecurityManager)SecurityUtils.getSecurityManager()).getConfiguration();

    public CerbAuthResponse authenticate(CerbNegotiationResponse negotiationResponse, CerbAuthRequest authRequest)
    {
        JerseyClientBuilder builder = new JerseyClientBuilder();
        ClientConfig cConfig = new ClientConfig();

        Client client = builder.newClient();

        WebTarget webTarget = client.target(configuration.getCerberusServer() + "/authenticate");

        Invocation.Builder invBuilder = webTarget.request();
        Response response = invBuilder.post(Entity.entity(authRequest, MediaType.APPLICATION_JSON_TYPE));

        CerbAuthResponse authResponse = (CerbAuthResponse)response.getEntity();

        return authResponse;

    }

    public Subject processResponse(CerbNegotiationResponse negotiationResponse, CerbAuthResponse response, String password)
    {
        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(configuration.getPBDKF2Iterations() * 8);

        byte[] key = null;
        byte[] salt = CryptoFunctions.combineArrays(negotiationResponse.getConfiguration().getApplicationSalt(), CryptoFunctions.combineArrays(negotiationResponse.getServiceSalt(), negotiationResponse.getUserSalt()));

        try
        {
            key = CryptoFunctions.pbkdf2(password.toCharArray(), salt, configuration.getPBDKF2Iterations(), configuration.getPBDKF2NumBytes());
        }
        catch (NoSuchAlgorithmException ex)
        {
            logger.error(ex.getMessage());
        }
        catch (InvalidKeySpecException ex)
        {
            logger.error(ex.getMessage());
        }

        //int sessionID = Integer.parseInt(sessionInfo[0]);
        //String sessionKey = sessionInfo[1];

        if(response.getResponseText().contains("succeeded"))
        {
            String sessionInfo = Base64.decodeToString(aesCipherService.decrypt(Base64.decode(response.getEncryptedSession()), key).toString());
            String[] sessionSplit = sessionInfo.split(":");
            response.getSubject().getSession().setAttribute(sessionSplit[0], sessionSplit[1]);
            return response.getSubject();
        }
        else
        {
            return null;
        }
    }
}

