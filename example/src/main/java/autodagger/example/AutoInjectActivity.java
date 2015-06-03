package autodagger.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import autodagger.AutoInjector;
import autodagger.example.BaseActivity;

/**
 * Created by chensimin on 15/6/3.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@AutoInjector(BaseActivity.class)
public @interface AutoInjectActivity {
}
