package bb.rackmesa.research.authorization;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Created by Dan on 3/26/2016.
 */
public class CerbAuthToken extends UsernamePasswordToken {

    private String service;

    public String getService()
    {
        return service;
    }

    public void setService(String service)
    {
        this.service = service;
    }

    public CerbAuthToken(String service, String user, String pass)
    {
        super(user, pass);
        setService(service);
    }
}
