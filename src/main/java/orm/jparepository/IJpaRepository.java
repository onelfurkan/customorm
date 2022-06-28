package orm.jparepository;


import orm.exception.EntityOperationException;
import orm.exception.UnsupportedFieldTypeException;

import java.sql.SQLException;
import java.util.List;

/**
 * JPA repository interface to perform base entity operations.
 *
 * @param <T>  parameterized entity type
 * @param <ID> parameterized entity id type
 */
public interface IJpaRepository<T,ID>
{
    void save(T pEntity) throws EntityOperationException;
    void remove(T Entity) throws EntityOperationException;
    List<T> findAll(Class<T> pEntityClassObject) throws EntityOperationException;
    T findById(Class<T> pEntityClassObject, ID pId) throws EntityOperationException;
    void update (T Entity) throws EntityOperationException;
}
