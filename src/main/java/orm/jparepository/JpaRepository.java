package orm.jparepository;


import orm.connection.DbConnectionProvider;
import orm.entity.Person;
import orm.exception.EntityOperationException;
import orm.exception.UnsupportedFieldTypeException;
import orm.sql.IQueryGenerator;
import orm.sql.IStatementGenerator;
import orm.utilities.EntityUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of JPA repository interface to perform base entity operartions.
 *
 * @param <T>  Parameterized entity type
 * @param <ID> Parameterized entity id type
 */
public class JpaRepository<T,ID> implements IJpaRepository<T,ID>
{
    private static final Logger logger = Logger.getLogger(JpaRepository.class.getSimpleName());

    // TODO: use @autowired annotation to inject dependencies
    private final IQueryGenerator<T> queryGenerator;
    private final IStatementGenerator<T,ID> statementGenerator;

    public JpaRepository(IQueryGenerator<T> queryGenerator,
                         IStatementGenerator<T,ID> statementGenerator)
    {
        this.queryGenerator = queryGenerator;
        this.statementGenerator = statementGenerator;
    }

    /**
     * Saves entity to db.
     *
     * @param pEntity    the entity object
     * @throws EntityOperationException exceptions that may occur while saving this entity to db like
     *                                  SQLException, ClassNotFoundException or others.
     */
    @Override
    public void save(T pEntity) throws EntityOperationException
    {
        try
        {
            Class<?> entityClassObject = pEntity.getClass();
            String sql = queryGenerator.createInsertQuery(entityClassObject);

            if(EntityUtil.isEntity(entityClassObject) && sql != null)
            {
                PreparedStatement preparedStatement =  statementGenerator.createInsertStatement(sql,pEntity);

                if(preparedStatement != null)
                {
                    preparedStatement.executeUpdate();
                }
            }
            else
            {
                logger.log(Level.WARNING,"Class "+pEntity.getClass().getName()+" is not a entity !" +
                        " Claas must have @Entity annotation to perform entity operations.");
            }
        }
        catch (Exception e)
        {
            throw new EntityOperationException("Entity could not be saved for "+pEntity.toString(),e);
        }

    }

    @Override
    public void remove(T pEntity) throws EntityOperationException
    {
        try
        {
            if(EntityUtil.isEntity(pEntity.getClass()))
            {
                String sql = queryGenerator.createDeleteQuery(pEntity.getClass());

                if(sql != null)
                {
                    PreparedStatement statement = statementGenerator.createDeleteStatement(sql,pEntity);
                    statement.executeUpdate();
                }
            }
            else
            {
                logger.log(Level.WARNING,"Class "+pEntity.getClass().getName()+" is not a entity !" +
                        " Claas must have @Entity annotation to perform entity operations.");
            }
        }
        catch (Exception e)
        {
            throw new EntityOperationException("Entity could not be removed for " +pEntity.toString(),e);
        }
    }

    @Override
    public List<T> findAll(Class<T> pEntityClassObject) throws EntityOperationException
    {
        List<T> results = new ArrayList<>();

        try
        {
            if(EntityUtil.isEntity(pEntityClassObject))
            {
                String sql = queryGenerator.createFindAllQuery(pEntityClassObject);

                if(sql != null)
                {
                    PreparedStatement statement = statementGenerator.createSelectAllIdStatement(sql);
                    ResultSet resultSet = statement.executeQuery();

                    if(resultSet != null)
                    {
                        while (resultSet.next())
                        {
                            T result = EntityUtil.mapToEntity(resultSet,pEntityClassObject);
                            results.add(result);
                        }
                    }
                }
            }
            else
            {
                logger.log(Level.WARNING,"Class "+pEntityClassObject.getName()+" is not a entity !" +
                        " Claas must have @Entity annotation to perform entity operations.");
            }
        }
        catch (Exception e)
        {
            throw new EntityOperationException("Query could not be executed !", e);
        }

        return results;
    }

    @Override
    public T findById(Class<T> pEntityClassObject, ID pId) throws EntityOperationException {
        T result = null;

        try
        {
            if(EntityUtil.isEntity(pEntityClassObject))
            {
                String sql = queryGenerator.createFindByIdQuery(pEntityClassObject);

                if(sql != null)
                {
                    PreparedStatement statement = statementGenerator.createSelectByIdStatement(sql,pId);
                    ResultSet resultSet = statement.executeQuery();

                    if(resultSet != null)
                    {
                        while (resultSet.next())
                        {
                            result = EntityUtil.mapToEntity(resultSet, pEntityClassObject);
                        }
                    }
                }
            }
            else
            {
                logger.log(Level.WARNING,"Class "+pEntityClassObject.getName()+" is not a entity !" +
                        " Claas must have @Entity annotation to perform entity operations.");
            }
        }
        catch (Exception e)
        {
            throw new EntityOperationException("Find entity operation is failed for id = "+pId,e);
        }

        return result;
    }

    @Override
    public void update(T pEntity) throws EntityOperationException {
        try
        {
            if(EntityUtil.isEntity(pEntity.getClass()))
            {
                String sql = queryGenerator.createUpdateQuery(pEntity.getClass());

                if(sql != null)
                {
                    PreparedStatement statement = statementGenerator.createUpdateStatement(sql,pEntity);
                    statement.executeUpdate();
                }
            }
            else
            {
                logger.log(Level.WARNING,"Class "+pEntity.getClass().getName()+" is not a entity !" +
                        " Claas must have @Entity annotation to perform entity operations.");
            }
        }
        catch (Exception e)
        {
            throw new EntityOperationException("Entity "+pEntity.toString()+ " could not be updated !",e);
        }
    }
}
