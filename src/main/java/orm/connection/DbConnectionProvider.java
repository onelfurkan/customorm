package orm.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class provides database connection.
 * For this example project, Mysql db was preferred.
 * Any other db can be used for the test.
 *
 */
public class DbConnectionProvider
{
    /** The static database connection fields. */
    private static Connection connection;

    /**
     * Private constructor to prevent object creation of this class.
     *
     */
    private DbConnectionProvider()
    {

    }

    /**
     * Gets database connection.
     *
     * @return the established database connection
     *
     * @throws SQLException           sql syntax or other sql exceptions
     * @throws ClassNotFoundException It is thrown when relevant class not found
     */
    public static Connection getDbConnection() throws SQLException, ClassNotFoundException
    {
        if (connection == null)
        {
            connection = connectToMysqlServer();
        }

        return connection;
    }

    /**
     * Connects to database using JDBC driver.
     *
     * @return the connection of Mysql Db.
     *
     * @throws ClassNotFoundException It is thrown when relevant class not found
     * @throws SQLException           sql syntax or other sql exceptions
     */
    private static Connection  connectToMysqlServer() throws ClassNotFoundException, SQLException
    {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/development","root","root");
    }

}
