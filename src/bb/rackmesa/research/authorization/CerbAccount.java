package bb.rackmesa.research.authorization;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Set;

/**
 * Created by Dan on 3/30/2016.
 */
public class CerbAccount extends SimpleAccount {

    private String service;
    private int userID;


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



    public CerbAccount(String service, PrincipalCollection principal, Object credential, String realmName, Set<String> roles, Set<Permission> permissions)
    {
        super(principal, credential, realmName, roles, permissions);
        this.service = service;
    }
}
