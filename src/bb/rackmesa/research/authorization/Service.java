package bb.rackmesa.research.authorization;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Dan on 3/27/2016.
 */
public class Service {
    private static Logger logger = LoggerFactory.getLogger(DatabaseFunctions.class);

    private int serviceID;
    private String name;
    private String owningUser;
    private List<String> users;
    private boolean isOpenPolicy;

    public void setName(String value)
    {
        name = value;
    }

    public String getName()
    {
        return name;
    }

    public void setOwningUser(String value)
    {
        owningUser = value;
    }

    public String getOwningUser()
    {
        return owningUser;
    }

    public void setUsers(List<String> value)
    {
        users = value;
    }

    public List<String> getUsers()
    {
        return users;
    }

    public void setServiceID(int value)
    {
        serviceID = value;
    }

    public int getServiceID()
    {
        return serviceID;
    }

    public void setIsOpenPolicy(boolean value)
    {
        isOpenPolicy = value;
    }

    public boolean getIsOpenPolicy()
    {
        return isOpenPolicy;
    }

}
