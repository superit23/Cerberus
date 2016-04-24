package bb.rackmesa.research.authorization;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionContext;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.subject.Subject;

import java.util.HashMap;

/**
 * Created by Dan on 4/19/2016.
 */
public class CerbServer {


    public CerbServer()
    {

    }

    public CerbAuthResponse authenticate(CerbAuthRequest request)
    {
        Subject subject = SecurityUtils.getSubject();
        subject.login(request);

        if(subject.isAuthenticated())
        {
            String sessionID = CryptoFunctions.generateUUID();
            String sessionKey = CryptoFunctions.generateUUID();

            HashMap<String, Object> sessionMap = new HashMap<String,Object>();
            sessionMap.put(sessionID, sessionKey);

            //subject.getSession().setAttribute(sessionID, sessionKey);

            CerbAuthResponse response = new CerbAuthResponse(sessionID, sessionKey, Base64.decode(DatabaseFunctions.getSecret(request.getService(), request.getUser()).getToken()), "Authentication succeeded for " + request.getUser(), subject);

            return response;
        }


        return new CerbAuthResponse("Authentication failed for " + request.getUser());

    }

    public CerbNegotiationResponse negotiate(String service, String username)
    {
        return new CerbNegotiationResponse(service, username, ((CerbSecurityManager)SecurityUtils.getSecurityManager()).getConfiguration());
    }
}
