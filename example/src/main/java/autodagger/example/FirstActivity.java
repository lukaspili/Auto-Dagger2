package autodagger.example;

import android.app.Activity;
import android.os.Bundle;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import dagger.Module;
import dagger.Provides;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoComponent(
        modules = {FirstActivity.ModuleOne.class, FirstActivity.ModuleTwo.class},
        dependencies = MyApp.class,
        superinterfaces = {HasDependenciesOne.class, HasDependenciesTwo.class}
)
@AutoInjector
@DaggerScope(FirstActivity.class)
public class FirstActivity extends Activity {

    private FirstActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = DaggerFirstActivityComponent.builder()
                .myAppComponent(((MyApp) getApplication()).getComponent())
                .moduleOne(new ModuleOne())
                .moduleTwo(new ModuleTwo())
                .build();
        component.inject(this);
    }

    @Module
    public static class ModuleOne {

        @Provides
        @DaggerScope(FirstActivity.class)
        public MyObject2<String, String> providesMyObject2() {
            return new MyObject2<>();
        }

        @Provides
        @DaggerScope(FirstActivity.class)
        public MyObject3 providesMyObject3() {
            return new MyObject3<>();
        }
    }

    @Module
    public static class ModuleTwo {

    }
}
