package bb.rackmesa.research.authorization;

import java.util.Date;

/**
 * Created by Dan on 4/19/2016.
 */
public class CerbPassInfo {
    private int user_id;
    private String token;
    private Date expiration;
    private byte[] salt;

    public  CerbPassInfo(int user_id,String token, Date expiration, byte[] salt)
    {
        this.token = token;
        this.expiration = expiration;
        this.salt = salt;
    }

    public void setUserID(int value)
    {
        user_id = value;
    }

    public void setToken(String value)
    {
        token = value;
    }

    public void setExpiration(Date value)
    {
        expiration = value;
    }

    public void setSalt(byte[] value)
    {
        salt = value;
    }

    public String getToken()
    {
        return token;
    }

    public Date getExpiration()
    {
        return expiration;
    }

    public byte[] getSalt()
    {
        return salt;
    }

    public int getUserId()
    {
        return user_id;
    }
}
