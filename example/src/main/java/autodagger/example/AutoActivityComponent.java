package autodagger.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import autodagger.AutoComponent;
import autodagger.AutoInjector;

/**
 * Created by chensimin on 15/6/3.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@AutoComponent(
        dependencies = ExampleApplication.class,
        modules = BaseActivity.Module.class,
        superinterfaces = {ExampleApplication.class, GlobalComponent.class})
public @interface AutoActivityComponent {
}
