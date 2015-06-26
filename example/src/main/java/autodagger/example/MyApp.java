package autodagger.example;

import android.app.Application;

import autodagger.AutoComponent;
import autodagger.AutoExpose;
import dagger.Provides;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoComponent(modules = MyApp.Module.class)
@DaggerScope(MyApp.class)
public class MyApp extends Application {

    private MyAppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerMyAppComponent.create();
    }

    public MyAppComponent getComponent() {
        return component;
    }

    @dagger.Module
    public static class Module {

        @Provides
        @AutoExpose(MyApp.class)
        @DaggerScope(MyApp.class)
        public RestClient2 providesRestClient2() {
            return new RestClient2();
        }
    }
}
