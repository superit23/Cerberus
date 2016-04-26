package bb.rackmesa.research.authorization;


import bb.rackmesa.research.authorization.Init;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dan on 3/18/2016.
 */
public class PBKDF2CredentialMatcherTest {

    @Test
    public void doCredentialsMatch() throws Exception {
        Init.Configure();

        UsernamePasswordToken token = new UsernamePasswordToken("testUser", "testPass");

        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
    }
}