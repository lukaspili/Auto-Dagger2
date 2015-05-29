package autodagger.example;

import javax.inject.Inject;

import autodagger.AutoExpose;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoExpose(MainActivity.class)
@DaggerScope(MainActivity.class)
public class SomeObject {

    @Inject
    public SomeObject() {
    }
}
