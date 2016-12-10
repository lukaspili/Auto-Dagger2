package autodagger.example;

import android.app.Activity;
import android.os.Bundle;

import javax.inject.Inject;
import javax.inject.Named;

import autodagger.AutoComponent;
import autodagger.AutoExpose;
import autodagger.AutoInjector;
import dagger.Module;
import dagger.Provides;

/**
 * Showcase: @AutoComponent
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoComponent(
        modules = {FirstActivity.ModuleOne.class, FirstActivity.ModuleTwo.class},
        dependencies = MyApp.class,
        superinterfaces = {HasDependenciesOne.class, HasDependenciesTwo.class},
        subcomponents = {MySubObject1.class, MySubObject2.class, MyManualSubcomponent.class}
)
@AutoInjector
@DaggerScope(FirstActivity.class)
public class FirstActivity extends Activity {

    private FirstActivityComponent component;
    @Inject MySubObject1 mySubObject1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = DaggerFirstActivityComponent.builder()
                .myAppComponent(((MyApp) getApplication()).getComponent())
                .moduleOne(new ModuleOne())
                .moduleTwo(new ModuleTwo())
                .build();
        component.inject(this);

        component.plusMySubObject2Component(new MySubObject2.Module(""), new MySubObject2.ModuleTwo()).plusMySubObject1Component().inject(mySubObject1);
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

        @Provides
        @DaggerScope(FirstActivity.class)
        @Named("1")
        @AutoExpose(FirstActivity.class)
        public MyObject4 providesMyObject4Qualifier1() {
            return new MyObject4();
        }

        @Provides
        @DaggerScope(FirstActivity.class)
        @Named("2")
        @AutoExpose(FirstActivity.class)
        public MyObject4 providesMyObject4Qualifier2() {
            return new MyObject4();
        }

        @Provides
        @DaggerScope(FirstActivity.class)
        public MySubObject1 providesMySubObject1() {
            return new MySubObject1();
        }
    }

    @Module
    public static class ModuleTwo {

    }
}
