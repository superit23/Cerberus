package bb.rackmesa.research.authorization;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.Permission;

import java.util.Set;

/**
 * Created by Dan on 3/30/2016.
 */
public class CerbAccount extends SimpleAccount {

    private String service;

    public String getService()
    {
        return service;
    }

    public void setService(String service)
    {
        this.service = service;
    }

    public CerbAccount(String service, Object principal, Object credential, String realmName, Set<String> roles, Set<Permission> permissions)
    {
        super(principal, credential, realmName, roles, permissions);
        this.service = service;
    }
}
