package bb.rackmesa.research.authorization;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;

/**
 * Created by Dan on 4/23/2016.
 */
public class CerbSecurityManager extends DefaultSecurityManager {

    private Configuration configuration;

    public void setConfiguration(Configuration value)
    {
        configuration = value;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public CerbSecurityManager(Realm realm)
    {
        super(realm);
        configuration = new Configuration();
    }
}
