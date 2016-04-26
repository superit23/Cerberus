package bb.rackmesa.research.authorization;

import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;

import static org.junit.Assert.*;

/**
 * Created by Dan on 3/13/2016.
 */
public class DatabaseFunctionsTest {

    private static Logger logger = LoggerFactory.getLogger(DatabaseFunctionsTest.class);


    @Test
    public void permissionCRUD() throws Exception {
        Init.Configure();

        Service cerberus = DatabaseFunctions.retrieveService("Cerberus");
        CerbAccount admin = DatabaseFunctions.retrieveUser(cerberus, "admin");
        CerbPermission printerPerm = DatabaseFunctions.createPermission(cerberus, "printer:query", "Allows the user to query printers.");

        assertTrue(cerberus.getPermissions().contains(printerPerm));

        printerPerm.setWildcardString("LESSBIGBOIS");
        DatabaseFunctions.updatePermission(printerPerm);

        DatabaseFunctions.associatePermissionWithUser(admin, printerPerm);

        admin = DatabaseFunctions.retrieveUser(cerberus, "admin");




        //CerbAuthToken token = new CerbAuthToken("Cerberus","admin", "admin");

        //Subject subject = SecurityUtils.getSubject();
        //subject.login(token);

        Subject subject = Init.EasyAuth("Cerberus","admin", "admin");

        //assertTrue(subject.isPermitted("LESSBIGBOIS"));
        subject.checkPermission("lessbigbois");
        logger.info(admin.getObjectPermissions().iterator().next().toString());
        subject.logout();

        DatabaseFunctions.unassociatePermissionWithUser(admin, printerPerm);

        //admin = DatabaseFunctions.retrieveUser(cerberus, "admin");
        Subject nSubject = Init.EasyAuth("Cerberus","admin", "admin");;

        assertFalse(subject.isPermitted("lessbigbois"));
        nSubject.logout();


        DatabaseFunctions.deletePermission(printerPerm);
    }

    @Test
    public void roleCRUD() throws Exception {
        Init.Configure();

        Service cerberus = DatabaseFunctions.retrieveService("Cerberus");
        CerbAccount admin = DatabaseFunctions.retrieveUser(cerberus, "admin");
        CerbRole role = DatabaseFunctions.createRole(cerberus, "BIGBOIS", "Allows the user to be a Big Boi.");

        assertTrue(cerberus.getRoles().contains(role));
        role.setValue("LESSBIGBOIS");
        DatabaseFunctions.updateRole(role);

        DatabaseFunctions.associateRoleWithUser(admin, role);

        admin = DatabaseFunctions.retrieveUser(cerberus, "admin");
        assertTrue(admin.getRoles().contains("LESSBIGBOIS"));
        logger.info("Now, we remove!");

        DatabaseFunctions.unassociateRoleWithUser(admin, role);

        admin = DatabaseFunctions.retrieveUser(cerberus, "admin");
        assertFalse(admin.getRoles().contains("LESSBIGBOIS"));


        //assertTrue(DatabaseFunctions.retrievePermissionsForService("Cerberus").contains(printerPerm));

        DatabaseFunctions.deleteRole(role);
    }

    @Test
    public void userCRUD() throws Exception {
        Init.Configure();

        Service cerberus = DatabaseFunctions.retrieveService("Cerberus");
        CerbAccount nUser = DatabaseFunctions.createUser(cerberus, "nUser", "durkaDurka", Date.valueOf("2018-04-20"));
        //CerbRole role = DatabaseFunctions.createRole(cerberus, "BIGBOIS", "Allows the user to be a Big Boi.");


        Subject subject = Init.EasyAuth("Cerberus","nUser", "durkaDurka");

        //CerbAuthToken token = new CerbAuthToken("Cerberus","nUser", "durkaDurka");


        //Subject subject = SecurityUtils.getSubject();
        //subject.login(token);

        assertTrue(cerberus.getUsers().contains(nUser));
        assertTrue(subject.isAuthenticated());
        //assertTrue(DatabaseFunctions.retrievePermissionsForService("Cerberus").contains(printerPerm));

        subject.logout();
        DatabaseFunctions.deleteUser(nUser);
    }

    @Test
    public void serviceCRUD() throws Exception {

        Init.Configure();

        Service cerberus = new Service();
        cerberus.setName("Cerberus");

        logger.info("Creating service");
        Service temp = DatabaseFunctions.createService("TEMPTest", DatabaseFunctions.retrieveUser(cerberus, "admin"), true);

        assertTrue(temp.getName() != null);

        logger.info("Updating service");
        temp.setName("CHANGEDTest");
        DatabaseFunctions.updateService(temp);


        logger.info("Retrieving changed service");
        Service temp2 = DatabaseFunctions.retrieveService("CHANGEDTest");

        assertTrue(temp2.getIsOpenPolicy() == true && temp2.getServiceID() == temp.getServiceID());
        temp = null;

        logger.info("Deleting service");
        DatabaseFunctions.deleteService(temp2);

    }





}