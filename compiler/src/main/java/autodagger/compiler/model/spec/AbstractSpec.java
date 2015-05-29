package autodagger.compiler.model.spec;

import com.squareup.javapoet.ClassName;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public abstract class AbstractSpec {

    protected final ClassName className;

    public AbstractSpec(ClassName className) {
        this.className = className;
    }

    public ClassName getClassName() {
        return className;
    }
}
