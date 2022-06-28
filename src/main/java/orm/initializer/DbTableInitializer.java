package orm.initializer;

import annotations.EnableTableCreation;
import orm.connection.DbConnectionProvider;
import orm.exception.NoExecutableClassFoundException;
import orm.sql.DDLQueryGenerator;
import orm.utilities.ClassObjectUtil;
import orm.utilities.EntityUtil;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class creates table of entities in db using custom annotations and reflection.
 */
public class DbTableInitializer implements IDbInitializer
{
    private static Logger logger = Logger.getLogger(DbTableInitializer.class.getSimpleName());
    private final DDLQueryGenerator ddlQueryGenerator;

    public DbTableInitializer(DDLQueryGenerator ddlQueryGenerator)
    {
        this.ddlQueryGenerator = ddlQueryGenerator;
    }

    /**
     * Creates db tables of entities using reflection and custom annotations.
     * It uses @EnableTableCreation annotation to scan entities and creates It's tables.
     * Methods only creates table If @EnableTableCreation isEnable() value is true.
     *
     * @throws NoExecutableClassFoundException Thrown If Application has no stater(main) class.
     * @throws SQLException           An exception that provides information on a database access
     *                                error or other errors.
     * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name
     *                                but no definition for the class with the specified name could be found.
     */
    @Override
    public void createEntityTables() throws NoExecutableClassFoundException, SQLException, ClassNotFoundException
    {
        List<Class<?>> mainClasses =  ClassObjectUtil.findMainClass();

        if(mainClasses.isEmpty())
        {
            throw new NoExecutableClassFoundException(" Application has not executable main class !");
        }

        for (Class<?> mainClass : mainClasses)
        {
            if(mainClass.isAnnotationPresent(EnableTableCreation.class))
            {
                EnableTableCreation enableTableCreation = mainClass.getAnnotation(EnableTableCreation.class);
                boolean isEnabled = enableTableCreation.isEnabled();
                if(isEnabled)
                {
                    String entityPackage = enableTableCreation.entityPackage();

                    if(!entityPackage.isEmpty())
                    {
                        Set<Class> entityClassObjects = ClassObjectUtil.findAllClassesOfPackage(entityPackage);
                        createTables(entityClassObjects);
                    }
                    else{
                        throw  new IllegalArgumentException("@EnableDbInit entityPackage argument is empty!");
                    }
                }
            }
        }
    }

    /**
     * Checks whether table is exist in the db.
     *
     * @param connection the db connection
     * @param tableName  the table name
     * @return           true, If table is exist in db
     *
     * @throws SQLException An exception that provides information on a database access
     *                      error or other errors.
     */
    @Override
    public boolean checkTableIfExist(Connection connection , String tableName) throws SQLException
    {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});
        return resultSet.next();
    }

    /**
     *  Creates tables of entities using DDL crate query.
     *
     * @param pEntityClassObjects the entity class object
     *
     * @throws SQLException           An exception that provides information on a database access
     *                                error or other errors.
     * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name
     *                                but no definition for the class with the specified name could be found.
     */
    private void createTables(Set<Class> pEntityClassObjects) throws SQLException, ClassNotFoundException {
        Connection connection = DbConnectionProvider.getDbConnection();

        for(Class<?> entityClassObject :  pEntityClassObjects)
        {
            String tableName = entityClassObject.getSimpleName().toUpperCase(Locale.ROOT);

            if(EntityUtil.isEntity(entityClassObject) && !checkTableIfExist(connection,tableName) )
            {
                String sql =  ddlQueryGenerator.createCreateTableQuery(entityClassObject);
                Statement preparedStatement = connection.createStatement();
                preparedStatement.executeUpdate(sql);

                if (checkTableIfExist(connection,tableName))
                {
                    logger.log(Level.INFO,"ORM : ["+sql+"] executed.");
                    logger.log(Level.INFO,"Table "+tableName+" was created successfully.");
                }
            }
        }
    }

}
