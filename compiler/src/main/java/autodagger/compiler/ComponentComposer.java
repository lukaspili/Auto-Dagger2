package autodagger.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import autodagger.compiler.model.spec.ExposedSpec;
import autodagger.compiler.model.spec.InjectorSpec;
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
        AnnotationSpec.Builder componentAnnotationBuilder = AnnotationSpec.builder(Component.class);

        if (!componentSpec.getDependenciesTypeNames().isEmpty()) {
            String member = buildAnnotationMember(componentSpec.getDependenciesTypeNames().size());
            componentAnnotationBuilder.addMember("dependencies", member, componentSpec.getDependenciesTypeNames().toArray());
        }

        if (!componentSpec.getModulesTypeNames().isEmpty()) {
            String member = buildAnnotationMember(componentSpec.getModulesTypeNames().size());
            componentAnnotationBuilder.addMember("modules", member, componentSpec.getModulesTypeNames().toArray());
        }

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(componentSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", AnnotationProcessor.class.getName())
                        .build())
                .addAnnotation(componentAnnotationBuilder.build());

        // superinterfaces
        for (TypeName typeName : componentSpec.getSuperinterfacesTypeNames()) {
            builder.addSuperinterface(typeName);
        }

        // scope if any
        if (componentSpec.getScopeAnnotationMirror() != null) {
            builder.addAnnotation(AnnotationSpec.get(componentSpec.getScopeAnnotationMirror()));
        }

        for (InjectorSpec injectorSpec : componentSpec.getInjectorSpecs()) {
            builder.addMethod(MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(injectorSpec.getTypeName(), injectorSpec.getName())
                    .build());
        }

        for (ExposedSpec exposedSpec : componentSpec.getExposedSpecs()) {
            builder.addMethod(MethodSpec.methodBuilder(exposedSpec.getName())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(exposedSpec.getTypeName())
                    .build());
        }

        TypeSpec spec = builder.build();


    }
}
