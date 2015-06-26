package autodagger.compiler;

import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.TypeName;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import autodagger.compiler.model.spec.InjectorSpec;
import autodagger.compiler.utils.AutoComponentClassNameUtil;
import processorworkflow.AbstractComposer;
import processorworkflow.AbstractProcessing;
import processorworkflow.Errors;
import processorworkflow.ProcessingBuilder;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ComponentProcessing extends AbstractProcessing<ComponentSpec, State> {

    public ComponentProcessing(Elements elements, Types types, Errors errors, State state) {
        super(elements, types, errors, state);
    }

    @Override
    public Set<Class<? extends Annotation>> supportedAnnotations() {
        Set set = ImmutableSet.of(AutoComponent.class);
        return set;
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

        ComponentSpec spec = new Builder(extractor, errors).build();
        if (errors.hasErrors()) {
            return;
        }

        specs.add(spec);
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
            return null;

            ComponentSpec componentSpec = new ComponentSpec(AutoComponentClassNameUtil.getComponentClassName(extractor.getElement()));
            componentSpec.setTargetTypeName(TypeName.get(extractor.getTargetTypeMirror()));
            componentSpec.setScopeAnnotationMirror(extractor.getScopeAnnotationTypeMirror());

            // injectors
            componentSpec.setInjectorSpecs(buildInjectorSpecs(componentExtractor, injectorExtractors));

            // exposed
            componentSpec.setExposedSpecs(buildExposedSpecs(componentExtractor, exposedExtractors));

            // dependencies
            componentSpec.setDependenciesTypeNames(buildDependenciesTypeNames(componentExtractor.getDependenciesTypeMirrors(), targetsTypeMirrors));

            // superinterfaces
            componentSpec.setSuperinterfacesTypeNames(buildDependenciesTypeNames(componentExtractor.getSuperinterfacesTypeMirrors(), targetsTypeMirrors));

            List<TypeName> modulesTypeNames = new ArrayList<>();
            if (componentExtractor.getModulesTypeMirrors() != null) {
                for (TypeMirror typeMirror : componentExtractor.getModulesTypeMirrors()) {
                    modulesTypeNames.add(TypeName.get(typeMirror));
                }
            }
            componentSpec.setModulesTypeNames(modulesTypeNames);
        }

        private List<AdditionSpec> getAdditions() {

            for (AdditionExtractor additionExtractor : state.getInjectorExtractors()) {
                for (TypeMirror typeMirror : additionExtractor.getTargetTypeMirrors()) {
                    if (!ProcessingUtils.areTypesEqual(extractor.getTargetTypeMirror(), typeMirror)) {
                        // this component is not targeted by this addition
                        continue;
                    }

                    String name = StringUtils.uncapitalize(extractor.getElement().getSimpleName().toString());
                    injectorSpecs.add(new InjectorSpec(name, TypeName.get(extractor.getElement().asType())));
                }
            }


            List<InjectorSpec> injectorSpecs = new ArrayList<>();

            for (AdditionExtractor extractor : state.getInjectorExtractors()) {
                // empty @AutoInjector must be on same target
                // compare on both element and target element in order to show correct error message
                if (extractor.getTargetTypeMirrors().isEmpty() &&
                        ProcessingUtils.compareTypeWithOneOfSeverals(extractor.getElement().asType(), componentExtractor.getTargetTypeMirror(), componentExtractor.getElement().asType())) {

                    // applies on target
                    if (!validateEmptyAutoAnnotation(componentExtractor, extractor.getElement(), AutoInjector.class)) {
                        continue;
                    }
                }

                // without value
                if (extractor.getTargetTypeMirrors().isEmpty() &&
                        ProcessingUtils.compareTypeWithOneOfSeverals(componentExtractor.getTargetTypeMirror(), extractor.getElement().asType())) {
                    String name = StringUtils.uncapitalize(extractor.getElement().getSimpleName().toString());
                    injectorSpecs.add(new InjectorSpec(name, TypeName.get(extractor.getElement().asType())));
                    continue;
                }

                if (!extractor.getTargetTypeMirrors().isEmpty()) {
                    for (TypeMirror typeMirror : extractor.getTargetTypeMirrors()) {
                        if (ProcessingUtils.compareTypeWithOneOfSeverals(componentExtractor.getTargetTypeMirror(), typeMirror)) {
                            String name = StringUtils.uncapitalize(extractor.getElement().getSimpleName().toString());
                            injectorSpecs.add(new InjectorSpec(name, TypeName.get(extractor.getElement().asType())));
                        }
                    }
                }
            }

            return injectorSpecs;
        }
    }
}
