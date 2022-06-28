package orm.initializer;

import orm.exception.NoExecutableClassFoundException;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDbInitializer
{
    void createEntityTables() throws NoExecutableClassFoundException, SQLException, ClassNotFoundException;
    boolean checkTableIfExist(Connection connection , String tableName) throws SQLException;
}
