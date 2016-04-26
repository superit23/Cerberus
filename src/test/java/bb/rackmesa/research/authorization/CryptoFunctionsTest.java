package bb.rackmesa.research.authorization;

import org.apache.shiro.SecurityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.codec.Base64;

import static org.junit.Assert.*;

/**
 * Created by Dan on 3/12/2016.
 */
public class CryptoFunctionsTest {

    private static Logger logger = LoggerFactory.getLogger(CryptoFunctionsTest.class);

    @Test
    public void testConvertByteToHex() throws Exception {

        byte[] salt = CryptoFunctions.generateSalt(64);

        logger.trace(CryptoFunctions.convertByteToHex(salt));
    }

    @Test
    public void testHashSHA512() throws Exception {

        logger.trace(CryptoFunctions.hashSHA512("test"));
    }

    @Test
    public void testGenerateSalt() throws Exception {

        byte[] salt = CryptoFunctions.generateSalt(4);

        logger.trace(Base64.encodeToString(salt));
    }

    @Test
    public void testPbkdf2() throws Exception {

        Init.Configure();

        Configuration configuration = ((CerbSecurityManager)SecurityUtils.getSecurityManager()).getConfiguration();

        byte[] salt = CryptoFunctions.generateSalt(8);
        //String uuid = CryptoFunctions.generateUUID();
        //String uuid = "3ab3054b-3944-44e4-8bfb-4663495f9f1a";
        //logger.trace(Base64.encodeToString(salt));
        //logger.trace(uuid);

        byte[] heteroSalt = CryptoFunctions.combineArrays(CryptoFunctions.combineArrays(configuration.getApplicationSalt(),  Base64.decode("0mxilA==")), Base64.decode("jDfeWw=="));
        logger.trace(Base64.encodeToString(CryptoFunctions.pbkdf2("admin".toCharArray(), heteroSalt, configuration.getPBDKF2Iterations(), configuration.getPBDKF2NumBytes())));
    }

    @Test
    public void testGenerateUUID() throws Exception {

        logger.trace(CryptoFunctions.generateUUID());
    }

    @Test
    public void testSlowEquals() throws Exception {

        assertTrue(CryptoFunctions.slowEquals("Hello World!".getBytes(), "Hello World!".getBytes()));
        assertFalse(CryptoFunctions.slowEquals("asdfasdfasdfasdfasdfa".getBytes(), "Hello World!".getBytes()));

    }
}