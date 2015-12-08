package autodagger.example;

import autodagger.AutoSubcomponent;

/**
 * Created by lukasz on 04/12/15.
 */
@AutoSubcomponent(modules = {MySubObject2.Module.class, MySubObject2.ModuleTwo.class}, superinterfaces = MySubObject2.MyInterface.class)
@DaggerScope(MySubObject2.class)
public class MySubObject2 {

    @dagger.Module
    public class Module {

    }

    @dagger.Module
    public class ModuleTwo {

    }

    public interface MyInterface {

    }
}
