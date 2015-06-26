package autodagger.example;

import autodagger.AutoComponent;
import autodagger.AutoInjector;

/**
 * Difference with StandardActivityComponent1 is that the @AutoInjector is applied
 * here
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoComponent(
        dependencies = MyApp.class,
        superinterfaces = {HasDependenciesOne.class, HasDependenciesTwo.class},
        modules = StandardModule.class
)
@AutoInjector
public @interface StandardActivityComponent2 {
}
