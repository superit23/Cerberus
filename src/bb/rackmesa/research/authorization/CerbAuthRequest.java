package bb.rackmesa.research.authorization;


import org.apache.shiro.crypto.AesCipherService;

/**
 * Created by Dan on 3/29/2016.
 */
public class CerbAuthRequest {

    private String service;
    private String user;
    private String requestBody;

    public String getService()
    {
        return service;
    }

    public void setService(String value)
    {
        service = value;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String value)
    {
        user = value;
    }

    //public byte[] getKey()
    //{
    //    return key;
    //}

    public void setRequestBody(byte[] key)
    {
        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(256);
        //requestBody =  "AUTH_REQUEST:" + service + ":" + user + ":" + aesCipherService.encrypt((service + ":" + user + ":" + System.currentTimeMillis()).getBytes(), key).toBase64();
        requestBody =  aesCipherService.encrypt((service + ":" + user + ":" + System.currentTimeMillis()).getBytes(), key).toBase64();

    }

    public String getRequestBody()
    {
        return requestBody;
    }



}
