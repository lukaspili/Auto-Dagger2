package autodagger.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class AdditionSpec {

    private final String name;
    private final TypeName typeName;
    private AnnotationSpec qualifierAnnotationSpec;

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

    public AnnotationSpec getQualifierAnnotationSpec() {
        return qualifierAnnotationSpec;
    }

    public void setQualifierAnnotationSpec(AnnotationSpec qualifierAnnotationSpec) {
        this.qualifierAnnotationSpec = qualifierAnnotationSpec;
    }
}
