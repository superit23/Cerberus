package bb.rackmesa.research.authorization;


import org.slf4j.LoggerFactory;

/**
 * Created by root on 3/12/16.
 */
public class Configuration {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Configuration.class);

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

}
