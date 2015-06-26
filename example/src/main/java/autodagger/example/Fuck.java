package autodagger.example;

import autodagger.AutoExpose;
import autodagger.AutoInjector;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoExpose
@AutoInjector
public class Fuck {

    @dagger.Component(dependencies = BaseActivityComponent.class)
    @DaggerScope(Component.class)
    public interface Component {

    }
}
