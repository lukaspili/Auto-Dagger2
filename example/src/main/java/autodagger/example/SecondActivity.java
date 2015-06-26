package autodagger.example;

import android.app.Activity;
import android.os.Bundle;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import dagger.Component;
import dagger.Module;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoComponent(
        modules = {SecondActivity.ModuleOne.class, SecondActivity.ModuleTwo.class},
        dependencies = SecondActivity.SomeOtherComponent.class,
        superinterfaces = {HasDependenciesOne.class, HasDependenciesTwo.class}
)
@AutoInjector
@DaggerScope(SecondActivity.class)
public class SecondActivity extends Activity {

    private SecondActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = DaggerSecondActivityComponent.builder()
                .someOtherComponent(DaggerSecondActivity_SomeOtherComponent.create())
                .moduleOne(new ModuleOne())
                .moduleTwo(new ModuleTwo())
                .build();
        component.inject(this);
    }

    @Module
    public static class ModuleOne {

    }

    @Module
    public static class ModuleTwo {

    }


    @Component
    @DaggerScope(SomeOtherComponent.class)
    public interface SomeOtherComponent {

    }
}
