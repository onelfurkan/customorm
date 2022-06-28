package orm.sql;

import orm.exception.UnsupportedFieldTypeException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The interface class that has method signature that creates
 * prepared statement for base entity operations.
 *
 * @param <T>  the parameterized entity type
 * @param <ID> the parameterized id type
 */
public interface IStatementGenerator<T,ID>
{
    PreparedStatement createInsertStatement(String sql, T entity) throws SQLException, ClassNotFoundException,UnsupportedFieldTypeException, IllegalAccessException;
    PreparedStatement createSelectByIdStatement(String sql,ID pId) throws SQLException, ClassNotFoundException;
    PreparedStatement createSelectAllIdStatement(String sql) throws SQLException, ClassNotFoundException;
    PreparedStatement createUpdateStatement(String sql, T entity) throws Exception;
    PreparedStatement createDeleteStatement(String sql, T entity) throws Exception;
}
