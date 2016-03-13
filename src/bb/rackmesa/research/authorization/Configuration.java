package bb.rackmesa.research.authorization;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by root on 3/12/16.
 */
public class Configuration {

    private static Logger logger = LogManager.getLogger(CryptoFunctions.class.getName());

    private String dbConnectionString;
    private static Configuration instance;


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

}
