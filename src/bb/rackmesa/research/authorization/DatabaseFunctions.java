package bb.rackmesa.research.authorization;

import java.sql.*;
import java.util.Dictionary;
import java.util.Map;
import java.util.HashMap;

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

    public static String getSecret(String service, String username)
    {
        ResultSet rs = retrieve("SELECT token FROM (Users u JOIN Users_Services us ON u.user_id = us.user_id) derived JOIN Services s ON derived.service_id = s.service_id WHERE username = ? AND service_name = ?;", new Object[] {username, service});

        try {
            return rs.getString(0);
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
            return null;
        }
    }

    public static Map<String, String> getPermissionsForUserByService(String service, String username)
    {
        ResultSet rs = retrieve("SELECT key, value, description FROM ((Users u JOIN Users_Permissions up ON u.user_id = up.user_id) d1 JOIN Permissions p ON d1.permission_id = p.permissions_id) d2 JOIN Services s ON s.service_id = d2.service_id WHERE username = ? AND service_name = ?;", new Object[] {username, service});
        HashMap<String,String> permissions = new HashMap<String, String>();
        try {

            while (rs.next()){
                permissions.put(rs.getString(0), rs.getString(1));
            }

            return permissions;
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
            return null;
        }

    }





}
