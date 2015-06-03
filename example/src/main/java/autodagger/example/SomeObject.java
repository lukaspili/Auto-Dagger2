package autodagger.example;

import javax.inject.Inject;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoExposeActivity()
@ScopeActivity()
public class SomeObject {

    @Inject
    public SomeObject() {
    }
}
