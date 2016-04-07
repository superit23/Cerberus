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

    private String service;
    private int userID;
    private byte[] salt;
    private Date tokenExpiration;


    public String getService()
    {
        return service;
    }

    public void setService(String value)
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


    public CerbAccount(String service, Object principal, Date tokenExpiration, Object credential, byte[] salt, String realmName, Set<String> roles, Set<Permission> permissions)
    {
        super(principal, credential, realmName, roles, permissions);
        this.service = service;
        this.salt = salt;
        this.tokenExpiration = tokenExpiration;
    }
}
