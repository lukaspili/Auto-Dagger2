package autodagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AutoExpose {

    Class<?>[] value() default void.class;

    Class<?>[] parameterizedTypes() default void.class;
}
