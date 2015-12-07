package autodagger.example;

import autodagger.AutoSubcomponent;

/**
 * Created by lukasz on 04/12/15.
 */
@AutoSubcomponent(addsTo = MyApp.class)
@DaggerScope(MySubObject1.class)
public class MySubObject1 {
}
