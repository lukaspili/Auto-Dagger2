package autodagger.compiler;

import com.squareup.javapoet.TypeName;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class AdditionSpec {

    private final String name;
    private final TypeName typeName;

    public AdditionSpec(String name, TypeName typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public enum Type {
        INJECTOR, EXPOSE
    }
}
