package annotations;

import java.lang.annotation.*;

/**
 * Declares a field of entity as a table column.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column
{

}
