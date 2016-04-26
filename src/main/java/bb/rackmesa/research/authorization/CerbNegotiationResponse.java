package bb.rackmesa.research.authorization;

import javax.xml.crypto.Data;

/**
 * Created by Dan on 4/23/2016.
 */
public class CerbNegotiationResponse {

    private Configuration configuration;
    private byte[] serviceSalt;
    private byte[] userSalt;

    public CerbNegotiationResponse(String service, String username, Configuration configuration)
    {
        setConfiguration(configuration);
        setUserSalt(DatabaseFunctions.getSecret(service, username).getSalt());
        setServiceSalt(DatabaseFunctions.retrieveService(service).getSalt());
    }

    public void setConfiguration(Configuration value)
    {
        configuration = value;
    }

    public void setServiceSalt(byte[] value)
    {
        serviceSalt = value;
    }

    public void setUserSalt(byte[] value)
    {
        userSalt = value;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public byte[] getServiceSalt()
    {
        return serviceSalt;
    }

    public byte[] getUserSalt()
    {
        return userSalt;
    }

}
