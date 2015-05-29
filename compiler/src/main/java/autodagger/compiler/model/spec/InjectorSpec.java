package autodagger.compiler.model.spec;

import com.squareup.javapoet.TypeName;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class InjectorSpec {

    private final String name;
    private final TypeName typeName;

    public InjectorSpec(String name, TypeName typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public TypeName getTypeName() {
        return typeName;
    }
}
