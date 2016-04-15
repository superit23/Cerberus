package bb.rackmesa.research.authorization;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Date;
import java.util.Set;

/**
 * Created by Dan on 3/30/2016.
 */
public class CerbAccount extends SimpleAccount {

    private Service service;
    private int userID;
    private byte[] salt;
    private Date tokenExpiration;


    public Service getService()
    {
        return service;
    }

    public void setService(Service value)
    {
        service = value;
    }

    public int getUserID()
    {
        return userID;
    }

    public void setUserID(int value)
    {
        userID = value;
    }

    public byte[] getSalt()
    {
        return salt;
    }

    public void setSalt(byte[] value)
    {
        salt = value;
    }

    public Date getTokenExpiration()
    {
        return tokenExpiration;
    }

    public void setTokenExpiration(Date value)
    {
        tokenExpiration = value;
    }


    public CerbAccount(Service service, Object principal, Date tokenExpiration, Object credential, byte[] salt, String realmName, Set<String> roles, Set<Permission> permissions)
    {
        super(principal, credential, realmName, roles, permissions);
        this.service = service;
        this.salt = salt;
        this.tokenExpiration = tokenExpiration;
    }

    @Override
    public boolean equals(Object o)
    {
        CerbAccount other = (CerbAccount)o;

        return other.getService() == this.getService() && other.getUserID() == this.getUserID();
    }
}
