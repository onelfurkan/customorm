package orm.sql;

import annotations.Column;
import annotations.Id;
import orm.connection.DbConnectionProvider;
import orm.exception.EntityOperationException;
import orm.exception.UnsupportedFieldTypeException;
import orm.utilities.IdGenerator;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Implementation class of Statement Generator interface.
 *  This class has methods to create prepared statement of base entity operations.
 *
 * @param <T>  the parameterized entity type
 * @param <ID> the parameterized id type
 */
public class ISqlStatementGenerator<T,ID> implements IStatementGenerator<T,ID>
{
    private static final Logger logger = Logger.getLogger(PreparedStatement.class.getSimpleName());

    /**
     * Creates insert prepared statement using reflection and custom annotations.
     *
     * @param sql     the insert query of entity in String form that needs to be parameterized
     * @param pEntity the entity object that will be inserted
     * @return        the insert prepared statement that can be executable
     *
     * @throws SQLException           An exception that provides information on a database access
     *                                error or other errors.
     * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name
     *                                but no definition for the class with the specified name could be found.
     * @throws UnsupportedFieldTypeException Thrown when a class field data type is unsupported.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application tries
     *                                to reflectively create an instance (other than an array),
     *                                set or get a field, or invoke a method, but the currently
     *                                executing method does not have access to the definition of
     *                                the specified class, field, method or constructor.
     */
    @Override
    public PreparedStatement createInsertStatement(String sql, T pEntity) throws SQLException,
            ClassNotFoundException, UnsupportedFieldTypeException, IllegalAccessException
    {
        PreparedStatement preparedStatement = null;
        Class<?> entityClassObject = pEntity.getClass();
        Field[] fields = entityClassObject.getDeclaredFields();
        String entityName =  entityClassObject.getSimpleName();

        if(!(fields.length == 0))
        {
            Connection connection = DbConnectionProvider.getDbConnection();
            preparedStatement = connection.prepareStatement(sql);
            int paramIndex = 2;

            for (Field field : fields)
            {
                field.setAccessible(true);
                if(field.isAnnotationPresent(Id.class))
                {
                    long id = IdGenerator.getId();
                    preparedStatement.setLong(1,id );
                }
                else if (field.isAnnotationPresent(Column.class))
                {
                    if(field.getType() == int.class )
                    {
                        preparedStatement.setInt(paramIndex, (Integer) field.get(pEntity));
                        paramIndex++;
                    }
                    else if( field.getType() == String.class)
                    {
                        preparedStatement.setString(paramIndex, (String) field.get(pEntity));
                        paramIndex++;
                    }
                    else if( field.getType() == Long.class)
                    {
                        preparedStatement.setLong(paramIndex, (Long) field.get(pEntity));
                    }
                    else {
                        throw new UnsupportedFieldTypeException(field.getName()+" field of "
                                +entityClassObject.getName()+" type is unsupported! "+field.getType());
                    }
                }
                else
                {
                    logger.log(Level.WARNING,field.getName()+" field of "+entityName+" has no @Column annotation !");
                }
            }
        }
        else
        {
            logger.log(Level.WARNING,"Entity "+entityName+" does not have any fields ! " +
                    "This entity could not be created !");
        }

        return preparedStatement;
    }

    /**
     * Creates findById prepared statement using reflection and custom annotations.
     *
     * @param sql the selectById query in String form that needs to be parameterized
     * @param pId the entity id
     * @return    findById prepared statement that can be executable
     *
     * @throws SQLException           An exception that provides information on a database access
     *                                error or other errors.
     * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name
     *                                but no definition for the class with the specified name could be found.
     */
    @Override
    public PreparedStatement createSelectByIdStatement(String sql,ID pId)
            throws SQLException, ClassNotFoundException
    {
        PreparedStatement statement =  DbConnectionProvider.getDbConnection().prepareStatement(sql);
        statement.setLong(1, (Long) pId);
        return statement;
    }

    /**
     * Creates findAll prepared statement.
     *
     * @param sql the selectAll query in String form
     * @return    findAll prepared statement that can be executable
     *
     * @throws SQLException           An exception that provides information on a database access
     *                                error or other errors.
     * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name
     *                                but no definition for the class with the specified name could be found.
     */
    @Override
    public PreparedStatement createSelectAllIdStatement(String sql) throws SQLException, ClassNotFoundException
    {
        return DbConnectionProvider.getDbConnection().prepareStatement(sql);
    }

    /**
     * Creates update prepared statement using reflection and custom annotations.
     *
     * @param sql    the update query in String form that needs to be parameterized
     * @param entity the entity that will be updated
     * @return       update prepared statement that can be executable
     *
     * @throws SQLException           An exception that provides information on a database access
     *                                error or other errors.
     * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name
     *                                but no definition for the class with the specified name could be found.
     * @throws UnsupportedFieldTypeException Thrown when a class field data type is unsupported.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application tries
     *                                to reflectively create an instance (other than an array),
     *                                set or get a field, or invoke a method, but the currently
     *                                executing method does not have access to the definition of
     *                                the specified class, field, method or constructor.
     * @throws EntityOperationException exceptions that may occur while saving this entity to db.
     */
    @Override
    public PreparedStatement createUpdateStatement(String sql, T entity) throws SQLException, ClassNotFoundException,
            UnsupportedFieldTypeException, EntityOperationException, IllegalAccessException
    {
        PreparedStatement statement = DbConnectionProvider.getDbConnection().prepareStatement(sql);
        Field[] fields = entity.getClass().getDeclaredFields();
        Long id = null;
        int paramIndex = 1;

        for(Field field :  fields)
        {
            field.setAccessible(true);

            if(field.isAnnotationPresent(Id.class))
            {
                id = (Long) field.get(entity);
            }
            else if(field.isAnnotationPresent(Column.class))
            {
                if(field.getType() == int.class )
                {
                    statement.setInt(paramIndex,field.getInt(entity));
                    paramIndex++;
                }
                else if( field.getType() == String.class)
                {
                    statement.setString(paramIndex, (String) field.get(entity));
                    paramIndex++;
                }
                else if( field.getType() == Long.class)
                {
                    statement.setLong(paramIndex,field.getLong(entity));
                }
                else {
                    throw new UnsupportedFieldTypeException(field.getName()+" field of "
                            +entity.getClass().getName()+" type is unsupported! "+field.getType());
                }
            }
        }

        if( id == null)
        {
            throw new EntityOperationException("Id of Entity is null for "+entity.getClass().getName());
        }

        statement.setLong(paramIndex,id);
        return statement;
    }

    /**
     * Creates delete prepared statement using reflection and custom annotations.
     *
     * @param sql    the delete query in String form that needs to be parameterized
     * @param entity delete prepared statement that can be executable
     * @return       delete prepared statement that can be executable
     *
     * @throws SQLException           An exception that provides information on a database access
     *                                error or other errors.
     * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name
     *                                but no definition for the class with the specified name could be found.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application tries
     *                                to reflectively create an instance (other than an array),
     *                                set or get a field, or invoke a method, but the currently
     *                                executing method does not have access to the definition of
     *                                the specified class, field, method or constructor.
     * @throws EntityOperationException exceptions that may occur while saving this entity to db.
     */
    @Override
    public PreparedStatement createDeleteStatement(String sql, T entity) throws
            SQLException, ClassNotFoundException, IllegalAccessException, EntityOperationException
    {
        Field[] fields =  entity.getClass().getDeclaredFields();
        Long id = null;

        for(Field field :  fields)
        {
            field.setAccessible(true);

            if(field.isAnnotationPresent(Id.class))
            {
                id = (Long) field.get(entity);
            }
        }

        if( id == null)
        {
            throw new EntityOperationException("Creating update statement is failed ! Id of Entity is null for "
                    +entity.getClass().getName());
        }

        PreparedStatement statement =  DbConnectionProvider.getDbConnection().prepareStatement(sql);
        statement.setLong(1,id);
        return statement;
    }
}
