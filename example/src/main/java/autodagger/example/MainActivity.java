package autodagger.example;

import android.app.Activity;
import android.os.Bundle;

import autodagger.AutoComponent;
import autodagger.AutoExpose;
import autodagger.AutoInjector;
import dagger.Provides;

@AutoComponent(
        dependencies = ExampleApplication.class,
        modules = MainActivity.Module.class,
        superinterfaces = {ExampleApplication.class, GlobalComponent.class})
@AutoInjector
@DaggerScope(MainActivity.class)
public class MainActivity extends Activity {

    private MainActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExampleApplicationComponent parentComponent = ((ExampleApplication) getApplication()).getComponent();
        component = DaggerMainActivityComponent.builder()
                .exampleApplicationComponent(parentComponent)
                .build();
    }

    @dagger.Module
    public static class Module {

        @Provides
        @DaggerScope(MainActivity.class)
        @AutoExpose(MainActivity.class)
        public SomeOtherObject providesSomeOtherObject() {
            return new SomeOtherObject();
        }
    }
}
