package bb.rackmesa.research.authorization;

import java.sql.*;
import java.util.Objects;

/**
 * Created by Dan on 3/12/2016.
 */
public class DatabaseFunctions {

    private static String connString = "";

    public static ResultSet retrieve(String query, Object[] params)
    {
        try {
            Connection conn = DriverManager.getConnection(connString);
            PreparedStatement stmt = conn.prepareStatement(query);

            for(int i = 0; i < params.length; i++)
            {
                stmt.setObject(i, params[i]);
            }


            ResultSet rs = stmt.executeQuery(stmt.toString());

            return rs;
        }
        catch (SQLException ex)
        {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public static void execute(String query, Object[] params)
    {
        try {
            Connection conn = DriverManager.getConnection(connString);
            PreparedStatement stmt = conn.prepareStatement(query);

            for(int i = 0; i < params.length; i++)
            {
                stmt.setObject(i, params[i]);
            }


            stmt.execute(stmt.toString());

        }
        catch (SQLException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

}
