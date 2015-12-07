package autodagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by lukasz on 03/12/15.
 */
@Target(ElementType.TYPE)
public @interface AutoSubcomponent {

    /**
     * Specify the components and/or the @AutoComponent targets that will contain the following generated subcomponent
     */
    Class<?>[] addsTo();

    Class<?>[] modules() default {};

    Class<?>[] superinterfaces() default {};
}