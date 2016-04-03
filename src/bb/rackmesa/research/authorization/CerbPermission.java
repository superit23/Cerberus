package bb.rackmesa.research.authorization;

import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * Created by Dan on 4/2/2016.
 */
public class CerbPermission extends WildcardPermission {

    private int permissionID;

    public void setPermissionID(int value)
    {
        permissionID = value;
    }

    public int getPermissionID()
    {
        return permissionID;
    }

    private String description;

    public void setDescription(String value)
    {
        description = value;
    }

    public String getDescription()
    {
        return description;
    }

    public CerbPermission(String wildcardString, int permissionID)
    {
        super(wildcardString);

        setPermissionID(permissionID);
    }

    public CerbPermission(String wildcardString)
    {
        super(wildcardString);

        setPermissionID(-1);
    }
}
