package bb.rackmesa.research.authorization;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;

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


            ResultSet rs = stmt.executeQuery();

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

    public static List<Permission> getPermissionsForUserByService(String service, String username)
    {
        ResultSet rs = retrieve("SELECT key, value, description FROM ((Users u JOIN Users_Permissions up ON u.user_id = up.user_id) d1 JOIN Permissions p ON d1.permission_id = p.permission_id) d2 JOIN Services s ON s.service_id = d2.service_id WHERE username = ? AND service_name = ?;", new Object[] {username, service});
        //HashMap<String,String> permissions = new HashMap<String, String>();
        List<Permission> permissions = new ArrayList<>();

        try {

            while (rs.next()){
                permissions.add(new WildcardPermission(rs.getString(1) + ":" + rs.getString(2)));
            }

            return permissions;
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
            return null;
        }

    }

    public static SimpleAccount getUserAuthFromDB(String service, String username)
    {
        String token = null;
        Date tokenExpiration = null;

        try {
            ResultSet rs = retrieve("SELECT token, token_expiration, can_recreate, is_open_policy FROM (Users u JOIN Users_Services us ON u.user_id = us.user_id) d1 JOIN Services s ON d1.service_id = s.service_id  WHERE username = ? AND service_name = ?;", new Object[]{username, service});
            rs.next();
            token = rs.getString(1);
            tokenExpiration = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString(2));

            if(tokenExpiration.before(new Date()))
            {
                return null;
            }

            SimpleAccount account = new SimpleAccount(username, token, service);
            account.addObjectPermissions(getPermissionsForUserByService(service,username));
            account.addObjectPermission(new WildcardPermission("canRecreate:" + rs.getString(3)));
            account.addObjectPermission(new WildcardPermission("isOpenPolicy:" + rs.getString(4)));

            return account;
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
        }
        catch (ParseException ex)
        {
            logger.error(ex.getMessage());
        }


        return  null;

    }



}
