package annotations;

import java.lang.annotation.*;

/**
 * This annotation is used as a primary key of table of relevant entity.
 * Annotation is declared top of field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id
{

}
