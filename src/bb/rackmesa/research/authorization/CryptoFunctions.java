package bb.rackmesa.research.authorization;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by Dan on 3/11/2016.
 */
// https://gist.github.com/jtan189/3804290
public class CryptoFunctions {

    static SecureRandom rand = new SecureRandom();
    private static Logger logger = LogManager.getLogger(CryptoFunctions.class.getName());

    public static String convertByteToHex(byte data[])
    {
        StringBuffer hexData = new StringBuffer();
        for (int byteIndex = 0; byteIndex < data.length; byteIndex++)
            hexData.append(Integer.toString((data[byteIndex] & 0xff) + 0x100, 16).substring(1));

        return hexData.toString();
    }

    public static String hashSHA512(String input)
    {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(input.getBytes());
            byte[] output = digest.digest();
            return convertByteToHex(output);
        }
        catch (NoSuchAlgorithmException ex)
        {
            //System.err.println(ex.getMessage());
            logger.error(ex.getMessage());
            return "ERROR No such algorithm";
        }
    }


    public static byte[] generateSalt(int numChars)
    {
        byte[] salt = new byte[numChars];
        rand.nextBytes(salt);

        return salt;
    }


    public static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithSHA512");
        return skf.generateSecret(spec).getEncoded();
    }


    public static String generateUUID()
    {
        return UUID.randomUUID().toString();
    }

    public static boolean slowEquals(byte[] a, byte[] b)
    {
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

}
