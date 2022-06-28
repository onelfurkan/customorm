package orm.sql;

import annotations.Column;
import orm.utilities.EntityUtil;
import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Implementation class of query generator that creates
 * base entity operation queries in string form.
 * Note that, Created slq queries needs to be parameterized.
 *
 * @param <T> parameterized entity type
 */
public class QueryGenerator<T> implements IQueryGenerator<T>
{
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String VALUES = " VALUES";
    private static final String BASE_SELECT = "SELECT * FROM ";
    private static final String WHERE = " WHERE ";
    private static final String DELETE = "DELETE FROM ";
    private static final String UPDATE = "UPDATE ";
    private static final String SET = " SET ";

    /**
     * Creates findById slq query of given entity in string
     * form that needs to be parameterized.
     *
     * @param pEntityClassObject the entity class object
     * @return                   the findById sql query in string form
     */
    @Override
    public String createFindByIdQuery(Class<?> pEntityClassObject)
    {
        String tableName = pEntityClassObject.getSimpleName();
        String idFieldName = EntityUtil.findIdFieldName(pEntityClassObject);
        return BASE_SELECT +tableName + WHERE+ idFieldName+"=?";
    }

    /**
     * Creates findAll slq query of given entity in string form.
     *
     * @param pEntityClassObject the entity class object
     * @return                   the findAll sql query in string form
     */
    @Override
    public String createFindAllQuery(Class<?> pEntityClassObject)
    {
        String tableName = pEntityClassObject.getSimpleName();
        return BASE_SELECT+tableName;
    }

    /**
     * Creates insert slq query of given entity in string
     * form that needs to be parameterized.
     *
     * @param pEntityClassObject the entity class object
     * @return                   the insert sql query in string form
     */
    @Override
    public String createInsertQuery(Class<?> pEntityClassObject)
    {
        String tableName = pEntityClassObject.getSimpleName().toLowerCase(Locale.ROOT);
        Field[] fields =  pEntityClassObject.getDeclaredFields();
        String query = null;

        if(fields.length > 0)
        {
            StringBuilder qMarks = new StringBuilder();
            StringBuilder values = new StringBuilder();
            qMarks.append(VALUES);
            values.append(tableName.toUpperCase(Locale.ROOT));

            for(int i=0; i< fields.length; i++)
            {
                fields[i].setAccessible(true);

                if(i == 0)
                {
                    qMarks.append("(?");
                    values.append("(").append(fields[i].getName());
                }
                else
                {
                    qMarks.append(",?");
                    values.append(",").append(fields[i].getName());
                }
            }
            qMarks.append(")");
            values.append(") ");
            query = INSERT_INTO + values + qMarks;
        }
        else
        {
            System.out.println("Entity "+tableName+" has no field !");
        }


        return query;
    }

    /**
     * Creates update slq query of given entity in string
     * form that needs to be parameterized.
     *
     * @param pEntityClassObject the entity class object
     * @return                   the update sql query in string form
     */
    @Override
    public String createUpdateQuery(Class<?> pEntityClassObject)
    {
        Field[] fields = pEntityClassObject.getDeclaredFields();
        String tableName = pEntityClassObject.getSimpleName();
        String idFieldName = EntityUtil.findIdFieldName(pEntityClassObject);
        StringBuilder values = new StringBuilder();

        int valueIndex = 0;

        for(Field field : fields)
        {
            field.setAccessible(true);

            if(field.isAnnotationPresent(Column.class))
            {
                if(valueIndex == 0 )
                {
                    values.append(field.getName()).append("=?");
                }
                else
                {
                    values.append(",").append(field.getName()).append("=?");
                }

                valueIndex++;
            }
        }

        return UPDATE + tableName + SET + values + WHERE +idFieldName+"=?";
    }

    /**
     * Creates delete slq query of given entity in string
     * form that needs to be parameterized.
     *
     * @param pEntityClassObject the entity class object
     * @return                   the delete sql query in string form
     */
    @Override
    public String createDeleteQuery(Class<?> pEntityClassObject)
    {
        String tableName = pEntityClassObject.getSimpleName();
        String idFieldName = EntityUtil.findIdFieldName(pEntityClassObject);
        return DELETE + tableName + WHERE + idFieldName+"=?";
    }
}
