package bb.rackmesa.research.authorization;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Dan on 3/27/2016.
 */
public class Service {
    private static Logger logger = LoggerFactory.getLogger(Service.class);

    private int serviceID;
    private String name;
    private CerbAccount owningUser;
    private List<CerbAccount> users;
    private boolean isOpenPolicy;
    private List<CerbRole> roles;
    private List<CerbPermission> permissions;
    private byte[] salt;

    public Service()
    {
        roles = new ArrayList<>();
        users = new ArrayList<>();
        permissions = new ArrayList<>();
    }

    public Service(String name, CerbAccount owningUser)
    {
        this();
        setName(name);
        setOwningUser(owningUser);
    }

    public void setName(String value)
    {
        name = value;
    }

    public String getName()
    {
        return name;
    }

    public void setOwningUser(CerbAccount value)
    {
        owningUser = value;
    }

    public CerbAccount getOwningUser()
    {
        return owningUser;
    }

    public void setUsers(List<CerbAccount> value)
    {
        users = value;
    }

    public List<CerbAccount> getUsers()
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

    public void setRoles(List<CerbRole> value)
    {
        roles = value;
    }

    public List<CerbRole> getRoles()
    {
        return roles;
    }

    public void setPermissions(List<CerbPermission> value)
    {
        permissions = value;
    }

    public List<CerbPermission> getPermissions()
    {
        return permissions;
    }

    public byte[] getSalt()
    {
        return salt;
    }

    public void setSalt(byte[] value)
    {
        salt = value;
    }

}
