package autodagger;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
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
     * Includes modules, dependencies and superinterfaces from an annotation that is
     * itself annotated with @AutoComponent
     */
    Class<? extends Annotation> includes() default Annotation.class;

    /**
     * Subcomponents to be declared inside this component
     */
    Class<?>[] subcomponents() default {};
}
