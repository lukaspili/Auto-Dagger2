package autodagger.compiler;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import autodagger.compiler.model.spec.ComponentSpec;
import autodagger.compiler.model.spec.ExposedSpec;
import autodagger.compiler.model.spec.InjectorSpec;
import dagger.Component;

/**
 * Actually it generates readable and understandable code!
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class MisunderstoodPoet {

    public TypeSpec compose(ComponentSpec componentSpec) {
        Preconditions.checkNotNull(componentSpec, "Component spec cannot be null");
        Preconditions.checkNotNull(componentSpec.getDependenciesTypeNames(), "Component spec dependencies cannot be null");
        Preconditions.checkNotNull(componentSpec.getModulesTypeNames(), "Component spec modules cannot be null");
        Preconditions.checkNotNull(componentSpec.getInjectorSpecs(), "Component spec injectors cannot be null");
        Preconditions.checkNotNull(componentSpec.getExposedSpecs(), "Component spec exposed cannot be null");

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

        return builder.build();
    }

    private String buildAnnotationMember(int count) {
        StringBuilder builder = new StringBuilder();
        if (count == 1) {
            builder.append("$T.class");
        } else {
            builder.append("{");
            for (int i = 0; i < count; ++i) {
                builder.append("$T.class");
                if (i < count - 1) {
                    builder.append(", ");
                }
            }
            builder.append("}");
        }

        return builder.toString();
    }
}
