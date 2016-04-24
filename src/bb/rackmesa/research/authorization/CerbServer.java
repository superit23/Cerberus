package bb.rackmesa.research.authorization;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
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
            DefaultSessionManager sessionManager = new DefaultSessionManager();


            String sessionID = CryptoFunctions.generateUUID();
            String sessionKey = CryptoFunctions.generateUUID();

            HashMap<String, Object> sessionMap = new HashMap<String,Object>();
            sessionMap.put(sessionID, sessionKey);

            sessionManager.getSessionFactory().createSession(new DefaultSessionContext(sessionMap));


            CerbAuthResponse response = new CerbAuthResponse(sessionID, sessionKey, (byte[])request.getCredentials(), "Authentication succeeded for" + request.getUser(), subject, request.getSalt());

            return response;
        }

        return new CerbAuthResponse("Authentication failed for " + request.getUser());

    }
}
