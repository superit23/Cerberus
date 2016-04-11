package bb.rackmesa.research.authorization;

import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * Created by Dan on 4/2/2016.
 */
public class CerbPermission extends WildcardPermission {

    private int permissionID;

    private String wildcardString;

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

    public void setWildcardString(String value)
    {
        wildcardString = value;
        this.setParts(wildcardString);

    }

    public String getWildcardString()
    {
        return wildcardString;
    }


    public CerbPermission(String wildcardString, int permissionID)
    {
        super(wildcardString);
        this.wildcardString = wildcardString;

        setPermissionID(permissionID);
    }

    public CerbPermission(String wildcardString)
    {
        super(wildcardString);

        setPermissionID(-1);
    }

    @Override
    public boolean equals(Object o)
    {
        CerbPermission other = (CerbPermission)o;

        return other.getPermissionID() == this.getPermissionID() && other.getDescription() == this.getDescription() && other.getWildcardString() == this.getWildcardString();
    }
}
