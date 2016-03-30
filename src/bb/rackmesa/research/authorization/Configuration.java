package bb.rackmesa.research.authorization;


import org.slf4j.LoggerFactory;

/**
 * Created by root on 3/12/16.
 */
public class Configuration {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Configuration.class);

    private String dbConnectionString;
    private static Configuration instance;

    private int pbkdf2Iterations;
    private int pbkdf2NumBytes;
    private byte[] applicationSalt;
    private long artificialWait;

    private String cerberusServer;


    public Configuration()
    {
        if(instance == null)
        {
            instance = this;
        }
        else
        {
            logger.warn("WARN The Configuration object is singleton; it cannot be instantiated again.");
        }
    }


    public static Configuration getInstance()
    {
        if(Configuration.instance == null)
            new Configuration();

        return instance;
    }

    public String getDbConnectionString()
    {
        return dbConnectionString;
    }

    public void setDbConnectionString(String value)
    {
        dbConnectionString = value;
    }

    public int getPBDKF2Iterations()
    {
        return pbkdf2Iterations;
    }

    public void setPBDKF2Iterations(int value)
    {
        pbkdf2Iterations = value;
    }

    public int getPBDKF2NumBytes()
    {
        return pbkdf2NumBytes;
    }

    public void setPBDKF2NumBytes(int value)
    {
        pbkdf2NumBytes = value;
    }

    public byte[] getApplicationSalt()
    {
        return applicationSalt;
    }

    public void setApplicationSalt(byte[] value)
    {
        applicationSalt = value;
    }

    public long getArtificialWait()
    {
        return artificialWait;
    }

    public void setArtificialWait(long value)
    {
        artificialWait = value;
    }

    public String getCerberusServer()
    {
        return cerberusServer;
    }

    public void setCerberusServer(String value)
    {
        cerberusServer = value;
    }


}
