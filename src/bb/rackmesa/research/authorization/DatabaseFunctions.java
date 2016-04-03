package bb.rackmesa.research.authorization;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.sun.javafx.collections.NonIterableChange;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;

import org.apache.shiro.codec.*;
import org.apache.shiro.crypto.CryptoException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Dan on 3/12/2016.
 */
public class DatabaseFunctions {

    private static Logger logger = LoggerFactory.getLogger(DatabaseFunctions.class);

    public static ResultSet retrieve(String query, Object[] params, int magic_constant)
    {
        try {
            Connection conn = DriverManager.getConnection(Configuration.getInstance().getDbConnectionString());
            PreparedStatement stmt = null;

            if(magic_constant != -1)
            {
                conn.prepareStatement(query, magic_constant);
            }
            else
            {
                conn.prepareStatement(query);
            }


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

    public static ResultSet retrieve(String query, Object[] params)
    {
        return retrieve(query, params, -1);
    }

    public static ResultSet insert(String query, Object[] params)
    {
        try {
            Connection conn = DriverManager.getConnection(Configuration.getInstance().getDbConnectionString());
            PreparedStatement stmt = null;
            conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);



            for(int i = 0; i < params.length; i++)
            {
                stmt.setObject(i + 1, params[i]);
            }


            int affectedRows = stmt.executeUpdate();

            if(affectedRows == 0)
            {
                throw new SQLException("Insert failed: No rows generated.");
            }

            return stmt.getGeneratedKeys();
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


            //stmt.execute();

            int affectedRows = stmt.executeUpdate();

            if(affectedRows == 0)
            {
                throw new SQLException("Execute failed: No rows affected.");
            }
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

    public static HashSet<Permission> getPermissionsForUserByService(String service, String username)
    {
        ResultSet rs = retrieve("SELECT value, description FROM ((Users u JOIN Users_Permissions up ON u.user_id = up.user_id) d1 JOIN Permissions p ON d1.permission_id = p.permission_id) d2 JOIN Services s ON s.service_id = d2.service_id WHERE username = ? AND service_name = ?;", new Object[] {username, service});
        //HashMap<String,String> permissions = new HashMap<String, String>();
        HashSet<Permission> permissions = new HashSet<>();

        try {

            while (rs.next()){
                permissions.add(new WildcardPermission(rs.getString(1)));
            }

            return permissions;
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
            return null;
        }

    }

    public static HashSet<String> getRolesForUserByService(String service, String username)
    {
        ResultSet rs = retrieve("SELECT value, description FROM ((Users u JOIN Users_Permissions up ON u.user_id = up.user_id) d1 JOIN Permissions p ON d1.permission_id = p.permission_id) d2 JOIN Services s ON s.service_id = d2.service_id WHERE username = ? AND service_name = ?;", new Object[] {username, service});

        HashSet<String> roles = new HashSet<>();

        try {

            while (rs.next()){
                roles.add(rs.getString(1));
            }

            return roles;
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
            return null;
        }
    }

    public static CerbAccount getUserAuthFromDB(String service, String username)
    {
        String token = null;
        Date tokenExpiration = null;
        int user_id = 0;

        try {
            ResultSet rs = retrieve("SELECT token, token_expiration, user_id FROM (Users u JOIN Users_Services us ON u.user_id = us.user_id) d1 JOIN Services s ON d1.service_id = s.service_id  WHERE username = ? AND service_name = ?;", new Object[]{username, service});

            try {
                rs.next();
            }
            catch (NullPointerException ex)
            {
                logger.warn("User " + username + " for service " + service + " does not exist.");
                return null;
            }


            token = rs.getString(1);
            tokenExpiration = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString(2));
            user_id = rs.getInt(3);

            if(tokenExpiration.before(new Date()))
            {
                String exceptionString = "User '" + username + "' for service '" + service + "' expired. Logon failed.";
                logger.warn(exceptionString);
                throw new ExpiredCredentialsException(exceptionString);
                //return null;
            }


            HashSet<Permission> permissions = getPermissionsForUserByService(service, username);
            HashSet<String> roles = getRolesForUserByService(service, username);

            SimplePrincipalCollection sPCollection = new SimplePrincipalCollection();

            sPCollection.add(username, "Cerberus");
            sPCollection.add(user_id, "Cerberus");

            CerbAccount account = new CerbAccount(service, sPCollection, token, "Cerberus", roles, permissions);
            account.setUserID(user_id);
            //account.addObjectPermission(new WildcardPermission("canRecreate:" + rs.getString(3)));
            //account.addObjectPermission(new WildcardPermission("isOpenPolicy:" + rs.getString(4)));

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

    public static void saveUserToDB(CerbAccount account)
    {
        if(account.getObjectPermissions() == null)
        {
            account.setObjectPermissions(new HashSet<Permission>());
        }

        if(account.getRoles() == null)
        {
            account.setRoles(new HashSet<String>());
        }

        CerbAccount dbAccount = getUserAuthFromDB(account.getService(), account.getPrincipals().getPrimaryPrincipal().toString());

        if(dbAccount == null)
        {

        }
    }

    public static CerbPermission createPermission(Service service, String value, String description)
    {
        ResultSet results = insert("INSERT INTO Permissions VALUES(DEFAULT,?,?,?);", new Object[] {service.getServiceID(), value, description});

        CerbPermission permission = new CerbPermission(value);
        permission.setDescription(description);

        try {
            if (results.next()) {
                permission.setPermissionID(results.getInt(1));
            }
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
        }

        return permission;

    }

    public static void createRole(Service service, String value, String description)
    {
        insert("INSERT INTO Roles VALUES(DEFAULT,?,?,?);", new Object[] {service.getServiceID(), value, description});

    }

    public static CerbAccount createUser(Service service, String username, Date tokenExpiration )
    {
        String uuid = CryptoFunctions.generateUUID();
        String token = null;

        try {
            token = org.apache.shiro.codec.Base64.encodeToString(CryptoFunctions.pbkdf2(uuid.toCharArray(), Configuration.getInstance().getApplicationSalt(), Configuration.getInstance().getPBDKF2Iterations(), Configuration.getInstance().getPBDKF2NumBytes()));
        }
        catch (NoSuchAlgorithmException ex)
        {
            logger.error(ex.getMessage());
        }
        catch (InvalidKeySpecException ex)
        {
            logger.error(ex.getMessage());
        }

        if(token == null)
        {
            throw new CryptoException("Token could not be created!");
        }

        ResultSet results = insert("INSERT INTO Users VALUES(DEFAULT,?,?,?,?);", new Object[] {service.getServiceID(), username, token, tokenExpiration});

        CerbAccount cerbUser = new CerbAccount(service.getName(), username, uuid, "Cerberus", new HashSet<String>(), new HashSet<Permission>());

        try {
            if (results.next()) {
                cerbUser.setUserID(results.getInt(1));
            }
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
        }

        return cerbUser;
    }

    public static Service createService(String name, CerbAccount owningUser, boolean isOpenPolicy)
    {
        ResultSet results = insert("INSERT INTO Services VALUES(DEFAULT,?,?,?);", new Object[] {name, owningUser.getUserID(), isOpenPolicy});

        Service service = new Service();

        try {
            if (results.next()) {
                service.setServiceID(results.getInt(1));
            }
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
        }

        return service;

    }

    public static void associatePermissionWithUser(CerbAccount user, CerbPermission permission)
    {
        execute("INSERT INTO Users_Permissions VALUES(DEFAULT,?,?);", new Object[] {user.getUserID(), permission.getPermissionID()});
    }

    public static void updateUser(CerbAccount user)
    {

    }

    public static void updateService(Service service)
    {

    }

    public static void updatePermission(CerbPermission permission)
    {

    }

    public static void updateRole(Service service, String value, String description)
    {
        execute("UPDATE Roles SET;", new Object[] {});
    }

}
