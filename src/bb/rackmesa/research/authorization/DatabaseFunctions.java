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
import org.apache.shiro.codec.Base64;
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
                stmt = conn.prepareStatement(query, magic_constant);
            }
            else
            {
                stmt = conn.prepareStatement(query);
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
        ResultSet rs = retrieve("SELECT value, description FROM ((Users u JOIN Users_Permissions up ON u.user_id = up.user_id) d1 JOIN Permissions p ON d1.permission_id = p.permission_id AND d1.service_id = p.service_id) d2 JOIN Services s ON s.service_id = d2.service_id WHERE username = ? AND service_name = ?;", new Object[] {username, service});
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

    public static CerbAccount retrieveUser(String service, String username)
    {
        String token = null;
        Date tokenExpiration = null;
        int user_id = 0;
        byte[] salt = null;

        try {
            ResultSet rs = retrieve("SELECT token, token_expiration, user_id, salt FROM (Users u JOIN Services s ON u.service_id = s.service_id) WHERE username = ? AND service_name = ?;", new Object[]{username, service});

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
            salt = Base64.decode(rs.getString(1));

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

            CerbAccount account = new CerbAccount(service, sPCollection, tokenExpiration, token, salt, "Cerberus", roles, permissions);
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

        CerbAccount dbAccount = retrieveUser(account.getService(), account.getPrincipals().getPrimaryPrincipal().toString());

        if(dbAccount != null)
        {

        }

        Collection<Permission> currPerms = account.getObjectPermissions();

        // If the user doesn't have the permission in the database, update the database with the new ones
        for (Permission perm :currPerms
             ) {

            CerbPermission castPerm = (CerbPermission)perm;

            for(Permission oldPerm : dbAccount.getObjectPermissions())
            {
                if(!(castPerm.getPermissionID() == ((CerbPermission)oldPerm).getPermissionID()))
                {
                    associatePermissionWithUser(account, castPerm);
                }
            }
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

    public static CerbAccount createUser(Service service, String username, String password, Date tokenExpiration)
    {
        //String uuid = CryptoFunctions.generateUUID();
        String token = null;

        try {
            token = org.apache.shiro.codec.Base64.encodeToString(CryptoFunctions.pbkdf2(password.toCharArray(), Configuration.getInstance().getApplicationSalt(), Configuration.getInstance().getPBDKF2Iterations(), Configuration.getInstance().getPBDKF2NumBytes()));
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

        CerbAccount cerbUser = new CerbAccount(service.getName(), username, tokenExpiration, token, CryptoFunctions.generateSalt(75), "Cerberus", new HashSet<String>(), new HashSet<Permission>());

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

    public static Service retrieveService(String name)
    {
        Service service = new Service();

        ResultSet servResults = retrieve("SELECT service_id, is_open_policy, username FROM Services s JOIN Users u ON s.owning_user = u.user_id WHERE name = ?;", new Object[] { name });

        int serviceID = 0;
        CerbAccount owningUser = null;
        boolean isOpenPolicy = false;
        List<CerbPermission> permissions = null;

        try {
            servResults.next();
            serviceID = servResults.getInt(1);
            isOpenPolicy = servResults.getBoolean(2);
            owningUser = retrieveUser(name, servResults.getString(3));
            permissions = retrievePermissionsForService(name);

        }
        catch (NullPointerException ex)
        {
            logger.warn("Service " + service + " does not exist.");
            return null;
        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
            return null;
        }

        service.setServiceID(serviceID);
        service.setIsOpenPolicy(isOpenPolicy);
        service.setOwningUser(owningUser);
        service.setName(name);
        service.setPermissions(permissions);

        return service;
    }

    public static List<CerbPermission> retrievePermissionsForService(String name)
    {
        ResultSet permResults = retrieve("SELECT permission_id, value, description FROM Permissions p JOIN Services s ON p.service_id = s.service_id WHERE s.service_name = ?;", new Object[] { name });

        List<CerbPermission> permissions = new ArrayList<>();

        try
        {

            while(permResults.next()) {

                int perm_id = permResults.getInt(1);
                String value = permResults.getString(2);
                String description = permResults.getString(3);

                CerbPermission permission = new CerbPermission(value, perm_id);
                permission.setDescription(description);

                permissions.add(permission);

            }

            return permissions;

        }
        catch (SQLException ex)
        {
            logger.error(ex.getMessage());
            return null;
        }
    }

    public static void associatePermissionWithUser(CerbAccount user, CerbPermission permission)
    {
        execute("INSERT INTO Users_Permissions VALUES(DEFAULT,?,?);", new Object[] {user.getUserID(), permission.getPermissionID()});
    }

    public static void associateRoleWithUser(CerbAccount user, CerbRole role)
    {
        execute("INSERT INTO Users_Roles VALUES(DEFAULT,?,?);", new Object[] {user.getUserID(), role.getRoleID()});
    }

    public static void unassociatePermissionWithUser(CerbAccount user, CerbPermission permission)
    {
        execute("DELETE FROM Users_Permissions WHERE user_id = ? AND permission_id = ?;", new Object[] {user.getUserID(), permission.getPermissionID()});
    }

    public static void unassociateRoleWithUser(CerbAccount user, CerbRole role)
    {
        execute("DELETE FROM Users_Roles WHERE user_id = ? AND role_id = ?;", new Object[] {user.getUserID(), role.getRoleID()});
    }

    public static void updateUser(CerbAccount user)
    {
        execute("UPDATE SET Users username = ?, token = ?, token_expiration = ?, salt = ? WHERE user_id = ?;", new Object[] { user.getPrincipals().getPrimaryPrincipal(), user.getCredentials(), user.getTokenExpiration(), user.getUserID()});
    }

    public static void updateService(Service service)
    {
        execute("UPDATE Services SET service_name  = ?, owning_user = ?, is_open_policy  = ? WHERE service_id = ?;", new Object[] {service.getName(), service.getOwningUser().getUserID(), service.getIsOpenPolicy(), service.getServiceID()});
    }

    public static void updatePermission(CerbPermission permission)
    {
        execute("UPDATE Permissions SET value = ?, description = ? WHERE permission_id = ?;", new Object[] {permission.toString(), permission.getDescription(), permission.getPermissionID()});
    }

    public static void updateRole(CerbRole role)
    {
        execute("UPDATE Roles SET value = ?, description = ? WHERE role_id = ?;", new Object[] {role.getValue(), role.getDescription(), role.getRoleID()});
    }

    public static void deleteUser(CerbAccount user)
    {
        execute("DELETE FROM Users WHERE user_id = ?;", new Object[] {user.getUserID()});
    }

    public static void deleteService(Service service)
    {
        execute("DELETE FROM Services WHERE service_id = ?;", new Object[] {service.getServiceID()});
    }

    public static void deleteRole(CerbRole role)
    {
        execute("DELETE FROM Roles WHERE role_id = ?;", new Object[] {role.getRoleID()});
    }

    public static void deletePermission(CerbPermission permission)
    {
        execute("DELETE FROM Permissions WHERE permission_id = ?;", new Object[] {permission.getPermissionID()});
    }

}
