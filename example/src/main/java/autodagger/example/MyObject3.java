package autodagger.example;

import autodagger.AutoExpose;
import autodagger.AutoInjector;

/**
 * With parameterized types
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoInjector(value = FirstActivity.class, parameterizedTypes = {String.class, String.class})
@AutoExpose(value = FirstActivity.class)
@DaggerScope(FirstActivity.class)
public class MyObject3<T, E> {

    private T t;
    private E e;
}
