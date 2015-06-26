package autodagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoComponent {

    Class<?>[] modules() default {};

    Class<?>[] dependencies() default {};

    Class<?>[] superinterfaces() default {};

    /**
     * Default is the class on which the @AutoComponent annotation is applied
     */
    Class<?> target() default void.class;

    /**
     * Apply modules, dependencies and superinterfaces from a template class that is
     * itself annotated with @AutoComponent
     */
    Class<?> fromTemplate() default void.class;
}
