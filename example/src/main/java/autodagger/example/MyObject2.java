package autodagger.example;

import autodagger.AutoExpose;
import autodagger.AutoInjector;

/**
 * Showcase: With parameterized types
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoInjector(value = {FirstActivity.class, SecondActivity.class}, parameterizedTypes = {String.class, String.class})
@AutoExpose(value = {FirstActivity.class, SecondActivity.class}, parameterizedTypes = {String.class, String.class})
@DaggerScope(FirstActivity.class)
public class MyObject2<T, E> {

    private T t;
    private E e;
}
