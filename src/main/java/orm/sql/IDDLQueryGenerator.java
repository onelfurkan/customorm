package orm.sql;

/**
 * DDL Query generator interface to create ddl sql query in string form.
 */
public interface IDDLQueryGenerator
{
    String createCreateTableQuery(Class<?> pClassObject) ;
    String createDropTableQuery(String pEntityClassName);
}
