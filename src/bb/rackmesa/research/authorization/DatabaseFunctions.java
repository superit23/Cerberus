package bb.rackmesa.research.authorization;

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Dan on 3/12/2016.
 */
public class DatabaseFunctions {

    private static Logger logger = LoggerFactory.getLogger(DatabaseFunctions.class);

    public static ResultSet retrieve(String query, Object[] params)
    {
        try {
            Connection conn = DriverManager.getConnection(Configuration.getInstance().getDbConnectionString());
            PreparedStatement stmt = conn.prepareStatement(query);

            for(int i = 0; i < params.length; i++)
            {
                stmt.setObject(i + 1, params[i]);
            }


            ResultSet rs = stmt.executeQuery(stmt.toString());

            return rs;
        }
        catch (SQLException ex)
        {
            //System.err.println(ex.getMessage());
            logger.error(ex.getMessage());
            return null;
        }
    }

    public static void execute(String query, Object[] params)
    {
        try {
            Connection conn = DriverManager.getConnection(Configuration.getInstance().getDbConnectionString());
            PreparedStatement stmt = conn.prepareStatement(query);

            for(int i = 0; i < params.length; i++)
            {
                stmt.setObject(i + 1, params[i]);
            }


            stmt.execute(stmt.toString());

        }
        catch (SQLException ex)
        {
            //System.err.println(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

}
