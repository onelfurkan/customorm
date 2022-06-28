package orm.sql;

import annotations.Id;
import orm.utilities.FieldUtil;
import java.lang.reflect.Field;
import java.util.Locale;

/**
 * This class creates Queries of DDL operations like create or
 * drop table operations using annotations and reflection.
 * Note that class just creates sql queries in String form,
 * not in sql statement form.
 */
public class DDLQueryGenerator implements IDDLQueryGenerator
{
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String DROP_TABLE = "DROP TABLE ";

    /**
     * Creates "Create Table" sql query of given entity using custom
     * annotations and reflection.
     *
     * @param pClassObject the class object of entity
     * @return             the sql create table query of entity in string form
     */
    @Override
    public String createCreateTableQuery(Class<?> pClassObject)
    {
        Field[] fields =  pClassObject.getDeclaredFields();
        String tableName = pClassObject.getSimpleName().toLowerCase(Locale.ROOT);
        StringBuilder sqlBuilder =  new StringBuilder();

        if(fields.length != 0)
        {
            sqlBuilder.append(CREATE_TABLE).append(tableName).append("(");

            Field primaryKeyField = null;

            for(int i = 0; i< fields.length; i++)
            {
                String dataTyeOfField = FieldUtil.findDataTypeOfField(fields[i]);

                if(i == 0)
                {
                    sqlBuilder.append(fields[i].getName()).append(" ").append(dataTyeOfField);
                }
                else
                {
                    fields[i].setAccessible(true);
                    sqlBuilder.append(",").append(fields[i].getName())
                            .append(" ").append(dataTyeOfField);
                }

                if(fields[i].isAnnotationPresent(Id.class))
                {
                    primaryKeyField = fields[i];
                }
            }

            if (null != primaryKeyField)
            {
                sqlBuilder.append(",primary key(").append(primaryKeyField.getName()).append(")");
            }
            sqlBuilder.append(")");
        }
        else
        {
            System.out.println("Entity "+tableName +" does not have any field !");
        }

        return sqlBuilder.toString();
    }

    @Override
    public String createDropTableQuery(String pEntityClassName)
    {
        return DROP_TABLE+pEntityClassName;
    }
}
