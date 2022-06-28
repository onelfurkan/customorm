package orm.utilities;

import annotations.Entity;
import annotations.Id;
import orm.exception.UnsupportedFieldTypeException;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides util methods for entity operation using reflection and custom annotation.
 */
public class EntityUtil
{
    private static final Logger logger = Logger.getLogger(EntityUtil.class.getSimpleName());

    /**
     * Private constructor to prevent object creation of this class.
     */
    private EntityUtil()
    {

    }

    /**
     * Finds Id field name of entity If present.
     *
     * @param pClassObject the entity class object
     * @return             the id field name of entity object
     */
    public static String findIdFieldName(Class<?> pClassObject)
    {
        String idFieldName = null;

        if(EntityUtil.isEntity(pClassObject))
        {
            Field[] fields = pClassObject.getDeclaredFields();

            for(Field field : fields)
            {
                if(field.isAnnotationPresent(Id.class))
                {
                    idFieldName = field.getName();
                }
            }
        }
        else
        {
            logger.log(Level.WARNING,pClassObject.getName()+" is not a entity ! It has not @Entity annotation.");
        }

        return idFieldName;
    }

    /**
     * Returns whether class is entity.
     *
     * @param pClassObject  the entity class object
     * @return              boolean info of whether class is entity
     */
    public static boolean isEntity(Class<?> pClassObject)
    {
        return pClassObject.isAnnotationPresent(Entity.class);
    }

    /**
     * Maps ResultSet to Entity object.
     *
     * @param pResultSet            the query resultSet
     * @param pEntityClassObject    the entity class object that will be mapped
     * @param <T>                   the entity type
     * @return                      the mapped entity object
     *
     * @throws InstantiationException Thrown when an application tries to create an instance of a
     *                                class using the newInstance method in class Class, but the
     *                                specified class object cannot be instantiated.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application tries
     *                                to reflectively create an instance (other than an array),
     *                                set or get a field, or invoke a method, but the currently
     *                                executing method does not have access to the definition of
     *                                the specified class, field, method or constructor.
     * @throws SQLException           An exception that provides information on a database access
     *                                error or other errors.
     * @throws UnsupportedFieldTypeException Thrown when a class field data type is unsupported.
     */
    public static <T> T mapToEntity(ResultSet pResultSet, Class<?> pEntityClassObject) throws InstantiationException,
            IllegalAccessException, SQLException, UnsupportedFieldTypeException
    {
        T entity = (T) pEntityClassObject.newInstance();
        Field[] fields  = entity.getClass().getDeclaredFields();

        for(Field field : fields)
        {
            field.setAccessible(true);

            if(field.getType() ==  Long.class )
            {
                field.set(entity,pResultSet.getLong(field.getName()));
            }
            else if(field.getType() == int.class)
            {
                field.set(entity,pResultSet.getInt(field.getName()));
            }
            else if(field.getType() == String.class)
            {
                field.set(entity,pResultSet.getString(field.getName()));
            }
            else if(field.getType() == double.class)
            {
                field.set(entity,pResultSet.getDouble(field.getName()));
            }
            else
            {
                throw new UnsupportedFieldTypeException(field.getType()+" field type is unsupported ! " +
                        "Mapping resultset to entity object is failed.");
            }

        }

        return entity;
    }
}
