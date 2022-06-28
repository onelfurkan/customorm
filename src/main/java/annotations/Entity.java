package annotations;

import java.lang.annotation.*;

/**
 * Maps this class object to a table in the database.
 * If this annotation is presented top of a class, This class will be mapped to a table in the database.
 * It is same as hibernate entity.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Entity
{

}
