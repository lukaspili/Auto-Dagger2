package autodagger.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import dagger.Component;
import processorworkflow.AbstractComposer;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ComponentComposer extends AbstractComposer<ComponentSpec> {

    public ComponentComposer(List<ComponentSpec> specs) {
        super(specs);
    }

    @Override
    protected JavaFile compose(ComponentSpec componentSpec) {
        AnnotationSpec.Builder annotationSpecBuilder = AnnotationSpec.builder(Component.class);

        for (TypeName typeName : componentSpec.getDependenciesTypeNames()) {
            annotationSpecBuilder.addMember("dependencies", "$T.class", typeName);
        }

        for (TypeName typeName : componentSpec.getModulesTypeNames()) {
            annotationSpecBuilder.addMember("modules", "$T.class", typeName);
        }

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(componentSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", AnnotationProcessor.class.getName())
                        .build())
                .addAnnotation(annotationSpecBuilder.build());

        for (TypeName typeName : componentSpec.getSuperinterfacesTypeNames()) {
            builder.addSuperinterface(typeName);
        }

        if (componentSpec.getScopeAnnotationSpec() != null) {
            builder.addAnnotation(componentSpec.getScopeAnnotationSpec());
        }

        for (AdditionSpec additionSpec : componentSpec.getInjectorSpecs()) {
            builder.addMethod(MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(additionSpec.getTypeName(), additionSpec.getName())
                    .build());
        }

        for (AdditionSpec additionSpec : componentSpec.getExposeSpecs()) {
            MethodSpec.Builder exposeBuilder = MethodSpec.methodBuilder(additionSpec.getName())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(additionSpec.getTypeName());
            if (additionSpec.getQualifierAnnotationSpec() != null) {
                exposeBuilder.addAnnotation(additionSpec.getQualifierAnnotationSpec());
            }
            builder.addMethod(exposeBuilder.build());
        }

        TypeSpec typeSpec = builder.build();
        return JavaFile.builder(componentSpec.getClassName().packageName(), typeSpec).build();
    }
}
