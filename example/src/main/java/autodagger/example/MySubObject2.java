package autodagger.example;

import autodagger.AutoSubcomponent;

/**
 * Created by lukasz on 04/12/15.
 */
@AutoSubcomponent(addsTo = FirstActivity.class, modules = MySubObject2.Module.class, superinterfaces = MySubObject2.MyInterface.class)
@DaggerScope(MySubObject2.class)
public class MySubObject2 {

    @dagger.Module
    public class Module {

    }

    public interface MyInterface {

    }
}
