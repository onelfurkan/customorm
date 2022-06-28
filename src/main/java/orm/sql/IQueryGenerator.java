package orm.sql;

/**
 * This interface has method signatures to create base
 * entity operation sql query in string form.
 *
 * @param <T> parameterized entity type
 */
public interface IQueryGenerator<T>
    {
    String createFindByIdQuery(Class<?> pEntityClassObject);
    String createFindAllQuery(Class<?> pEntityClassObject);
    String createInsertQuery(Class<?> pEntityClassObject);
    String createUpdateQuery(Class<?> pEntityClassObject);
    String createDeleteQuery(Class<?> pEntityClassObject);
}
