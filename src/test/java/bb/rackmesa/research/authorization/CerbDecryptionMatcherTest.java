package bb.rackmesa.research.authorization;

import bb.rackmesa.research.authorization.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dan on 4/19/2016.
 */
public class CerbDecryptionMatcherTest {
    @Test
    public void doCredentialsMatch() throws Exception {

        Init.Configure();

        CerbServer cerbServer = new CerbServer();
        CerbClient cerbClient = new CerbClient();

        CerbNegotiationResponse negotiationResponse = cerbServer.negotiate("Cerberus", "admin");

        CerbAuthRequest request = new CerbAuthRequest(negotiationResponse, "Cerberus", "admin", "admin");

        CerbAuthResponse response = cerbServer.authenticate(request);
        Subject subject = cerbClient.processResponse(negotiationResponse, response, "admin");

        assertTrue(subject.isAuthenticated());

    }

}