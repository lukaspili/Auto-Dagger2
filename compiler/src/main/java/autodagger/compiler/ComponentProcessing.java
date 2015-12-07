package autodagger.compiler;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.AutoComponent;
import autodagger.compiler.utils.AutoComponentClassNameUtil;
import processorworkflow.AbstractComposer;
import processorworkflow.AbstractProcessing;
import processorworkflow.Errors;
import processorworkflow.Logger;
import processorworkflow.ProcessingBuilder;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ComponentProcessing extends AbstractProcessing<ComponentSpec, State> {

    /**
     * Build all extractors first, then build all builders, because
     * we want to gather all targets first
     */
    private final Set<ComponentExtractor> extractors;

    public ComponentProcessing(Elements elements, Types types, Errors errors, State state) {
        super(elements, types, errors, state);
        extractors = new HashSet<>();
    }

    @Override
    public Set<Class<? extends Annotation>> supportedAnnotations() {
        Set set = ImmutableSet.of(AutoComponent.class);
        return set;
    }

    @Override
    protected void processElements(Set<? extends Element> annotationElements) {
        super.processElements(annotationElements);
        if (errors.hasErrors()) {
            return;
        }

        processExtractors();
    }

    @Override
    public boolean processElement(Element element, Errors.ElementErrors elementErrors) {
        if (ElementKind.ANNOTATION_TYPE.equals(element.getKind())) {
            // @AutoComponent is applied on another annotation, find out the targets of that annotation
            Set<? extends Element> targetElements = roundEnvironment.getElementsAnnotatedWith(MoreElements.asType(element));
            for (Element targetElement : targetElements) {
                process(targetElement, element);
                if (errors.hasErrors()) {
                    return false;
                }
            }
            return true;
        }

        process(element, element);

        if (errors.hasErrors()) {
            return false;
        }

        return true;
    }

    private void process(Element targetElement, Element element) {
        ComponentExtractor extractor = new ComponentExtractor(targetElement, element, types, elements, errors);
        if (errors.hasErrors()) {
            return;
        }

        extractors.add(extractor);
    }

    private void processExtractors() {
        for (ComponentExtractor extractor : extractors) {
            ComponentSpec spec = new Builder(extractor, errors).build();
            if (errors.hasErrors()) {
                return;
            }

            specs.add(spec);
        }
    }


    @Override
    public AbstractComposer<ComponentSpec> createComposer() {
        return new ComponentComposer(specs);
    }

    private class Builder extends ProcessingBuilder<ComponentExtractor, ComponentSpec> {

        public Builder(ComponentExtractor extractor, Errors errors) {
            super(extractor, errors);
        }

        @Override
        protected ComponentSpec build() {
            ComponentSpec componentSpec = new ComponentSpec(AutoComponentClassNameUtil.getComponentClassName(extractor.getComponentElement()));
            componentSpec.setTargetTypeName(TypeName.get(extractor.getTargetTypeMirror()));

            if (extractor.getScopeAnnotationTypeMirror() != null) {
                componentSpec.setScopeAnnotationSpec(AnnotationSpec.get(extractor.getScopeAnnotationTypeMirror()));
            }

            // injectors
            componentSpec.setInjectorSpecs(ProcessingUtil.getAdditions(extractor.getTargetTypeMirror(), state.getInjectorExtractors()));

            // exposed
            componentSpec.setExposeSpecs(ProcessingUtil.getAdditions(extractor.getTargetTypeMirror(), state.getExposeExtractors()));

            // dependencies
            componentSpec.setDependenciesTypeNames(getDependencies());

            // superinterfaces
            componentSpec.setSuperinterfacesTypeNames(ProcessingUtil.getTypeNames(extractor.getSuperinterfacesTypeMirrors()));

            // modules
            componentSpec.setModulesTypeNames(ProcessingUtil.getTypeNames(extractor.getModulesTypeMirrors()));

            // subcomponents
            componentSpec.setSubcomponentsMethodSpecs(getSubcomponents());

            return componentSpec;
        }

        private List<TypeName> getDependencies() {
            List<TypeName> typeNames = new ArrayList<>();
            if (extractor.getDependenciesTypeMirrors() == null) {
                return typeNames;
            }

            mainLoop:
            for (TypeMirror typeMirror : extractor.getDependenciesTypeMirrors()) {
                // check if dependency type mirror references an @AutoComponent target
                // if so, build the TypeName that matches the target component
                for (ComponentExtractor componentExtractor : extractors) {
                    if (componentExtractor == extractor) {
                        // ignore self
                        continue;
                    }

                    if (ProcessingUtil.areTypesEqual(componentExtractor.getTargetTypeMirror(), typeMirror)) {
                        typeNames.add(AutoComponentClassNameUtil.getComponentClassName(componentExtractor.getComponentElement()));
                        continue mainLoop;
                    }
                }

                typeNames.add(TypeName.get(typeMirror));
            }

            return typeNames;
        }

        private List<MethodSpec> getSubcomponents() {
            List<SubcomponentExtractor> subcomponentExtractors = state.getSubcomponentExtractors(extractor.getTargetTypeMirror());
            if (subcomponentExtractors == null) {
                return Collections.emptyList();
            }

            List<MethodSpec> methodSpecs = new ArrayList<>(subcomponentExtractors.size());
            for (SubcomponentExtractor subcomponentExtractor : subcomponentExtractors) {
                ClassName className = AutoComponentClassNameUtil.getComponentClassName(subcomponentExtractor.getElement());

                List<ParameterSpec> parameterSpecs = new ArrayList<>(subcomponentExtractor.getModulesTypeMirrors().size());
                int count = 0;
                for (TypeMirror typeMirror : subcomponentExtractor.getModulesTypeMirrors()) {
                    parameterSpecs.add(ParameterSpec.builder(TypeName.get(typeMirror), String.format("module%d", ++count)).build());
                }

                methodSpecs.add(MethodSpec.methodBuilder("plus" + className.simpleName())
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameters(parameterSpecs)
                        .returns(className)
                        .build());
            }

            return methodSpecs;
        }
    }
}
