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
import dagger.Subcomponent;
import processorworkflow.AbstractComposer;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class SubcomponentComposer extends AbstractComposer<SubcomponentSpec> {

    public SubcomponentComposer(List<SubcomponentSpec> specs) {
        super(specs);
    }

    @Override
    protected JavaFile compose(SubcomponentSpec subcomponentSpec) {
        AnnotationSpec.Builder annotationSpecBuilder = AnnotationSpec.builder(Subcomponent.class);

        for (TypeName typeName : subcomponentSpec.getModulesTypeNames()) {
            annotationSpecBuilder.addMember("modules", "$T.class", typeName);
        }

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(subcomponentSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", AnnotationProcessor.class.getName())
                        .build())
                .addAnnotation(annotationSpecBuilder.build());

        for (TypeName typeName : subcomponentSpec.getSuperinterfacesTypeNames()) {
            builder.addSuperinterface(typeName);
        }

        if (subcomponentSpec.getScopeAnnotationSpec() != null) {
            builder.addAnnotation(subcomponentSpec.getScopeAnnotationSpec());
        }

        for (AdditionSpec additionSpec : subcomponentSpec.getInjectorSpecs()) {
            builder.addMethod(MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(additionSpec.getTypeName(), additionSpec.getName())
                    .build());
        }

        for (AdditionSpec additionSpec : subcomponentSpec.getExposeSpecs()) {
            MethodSpec.Builder exposeBuilder = MethodSpec.methodBuilder(additionSpec.getName())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(additionSpec.getTypeName());
            if (additionSpec.getQualifierAnnotationSpec() != null) {
                exposeBuilder.addAnnotation(additionSpec.getQualifierAnnotationSpec());
            }
            builder.addMethod(exposeBuilder.build());
        }

        if (!subcomponentSpec.getSubcomponentsSpecs().isEmpty()) {
            builder.addMethods(subcomponentSpec.getSubcomponentsSpecs());
        }

        TypeSpec typeSpec = builder.build();
        return JavaFile.builder(subcomponentSpec.getClassName().packageName(), typeSpec).build();
    }
}