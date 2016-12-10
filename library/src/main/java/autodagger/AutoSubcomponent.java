package autodagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by lukasz on 03/12/15.
 */
@Target(ElementType.TYPE)
public @interface AutoSubcomponent {

    Class<?>[] modules() default {};

    Class<?>[] superinterfaces() default {};

    /**
     * Subcomponents to be declared inside this component
     */
    Class<?>[] subcomponents() default {};
}