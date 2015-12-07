package autodagger;

/**
 * Created by lukasz on 03/12/15.
 */
public @interface AutoSubcomponent {

    Class<?>[] addsTo();

    Class<?>[] modules() default {};

    Class<?>[] superinterfaces() default {};
}