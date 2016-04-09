package bb.rackmesa.research.authorization;

/**
 * Created by Dan on 4/3/2016.
 */
public class CerbRole {

    private String value;
    private String description;
    private int roleID;

    public CerbRole(String value, String description, int roleID)
    {
        setValue(value);
        setDescription(description);
        setRoleID(roleID);
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setDescription(String value)
    {
        description = value;
    }

    public String getDescription()
    {
        return description;
    }

    public void setRoleID(int value)
    {
        roleID = value;
    }

    public int getRoleID()
    {
        return roleID;
    }

    @Override
    public boolean equals(Object o)
    {
        CerbRole other = (CerbRole)o;

        return other.getRoleID() == this.getRoleID() && other.getDescription() == this.getDescription() && other.getValue() == this.getValue();
    }
}
