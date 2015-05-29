package autodagger.example;

import android.app.Application;

import autodagger.AutoComponent;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoComponent(
        modules = {ExampleApplication.Module1.class, ExampleApplication.Module2.class}
)
@DaggerScope(ExampleApplication.class)
public class ExampleApplication extends Application {

    private ExampleApplicationComponent component;

    @Override
    public void onCreate() {

        component = DaggerExampleApplicationComponent.builder()
                .module1(new Module1())
                .module2(new Module2())
                .build();
    }

    public ExampleApplicationComponent getComponent() {
        return component;
    }

    @dagger.Module
    public static class Module1 {

    }

    @dagger.Module
    public static class Module2 {

    }
}
