package autodagger.example;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@Scope
@Retention(RetentionPolicy.SOURCE)
public @interface DaggerScope {
    Class<?> value();
}
