package annotations;

import java.lang.annotation.*;

/**
 * This annotation is used to create table of entities in db like hibernate or other ORM frameworks.
 * Annotation is declared with the application starter(main class of app) class.
 * Two values are passed to this annotation.
 * These values are automatic table creation and the package of entities.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableTableCreation
{
    /**
     * This value stores the activation info of automatic table creation by the orm.
     * If true, Db tables of entities are created by the orm.
     * Default value is true.
     *
     * @return the info whether orm will creates db table of entities automatically
     */
    boolean isEnabled() default true;

    /**
     * The package of entities that will be scanned by the orm to create db tables.
     *
     * @return thw package of entities
     */
    String  entityPackage() default "";
}
