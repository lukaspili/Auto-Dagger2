package autodagger.example;

import autodagger.AutoInjector;
import autodagger.AutoSubcomponent;

/**
 * Created by lukasz on 04/12/15.
 */
@AutoSubcomponent(
        modules = {MySubObject2.Module.class, MySubObject2.ModuleTwo.class},
        superinterfaces = MySubObject2.MyInterface.class,
        subcomponents = MySubObject1.class
)
@AutoInjector
@DaggerScope(MySubObject2.class)
public class MySubObject2 {

    @dagger.Module
    public static class Module {
        public Module(String string) {
        }
    }

    @dagger.Module
    public static class ModuleTwo {

    }

    public interface MyInterface {

    }
}
