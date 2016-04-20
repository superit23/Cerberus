package bb.rackmesa.research.authorization;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import java.util.HashMap;

/**
 * Created by Dan on 4/19/2016.
 */
public class CerbServer {

    private HashMap<String, String> sessionMappings;

    public CerbServer()
    {
        sessionMappings = new HashMap<String, String>();
    }

    public CerbAuthResponse authenticate(CerbAuthRequest request)
    {
        Subject subject = SecurityUtils.getSubject();
        subject.login(request);

        if(subject.isAuthenticated())
        {
            String sessionID = CryptoFunctions.generateUUID();
            String sessionKey = CryptoFunctions.generateUUID();

            sessionMappings.put(sessionID, sessionKey);

            CerbAuthResponse response = new CerbAuthResponse(sessionID, sessionKey, (byte[])request.getCredentials(), "Authentication succeeded for" + request.getUser(), null);

            return response;
        }

        return new CerbAuthResponse("Authentication failed for " + request.getUser());

    }
}
