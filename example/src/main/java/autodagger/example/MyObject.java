package autodagger.example;

import javax.inject.Inject;

import autodagger.AutoExpose;
import autodagger.AutoInjector;

/**
 * With a double @AutoInjector & @AutoExpose
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoInjector(FirstActivity.class)
@AutoExpose(FirstActivity.class)
@DaggerScope(FirstActivity.class)
public class MyObject {

    @Inject
    public MyObject() {
    }
}
