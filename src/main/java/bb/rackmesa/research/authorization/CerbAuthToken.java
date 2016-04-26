package bb.rackmesa.research.authorization;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Created by Dan on 3/26/2016.
 */
public class CerbAuthToken extends UsernamePasswordToken {

    private Service service;

    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    public CerbAuthToken(String service, String user, String pass)
    {
        super(user, pass);
        setService(DatabaseFunctions.retrieveService(service));
    }
}
