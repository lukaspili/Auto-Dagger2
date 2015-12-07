package autodagger.compiler;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.AnnotationSpec;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.AutoSubcomponent;
import autodagger.compiler.utils.AutoComponentClassNameUtil;
import processorworkflow.AbstractComposer;
import processorworkflow.AbstractProcessing;
import processorworkflow.Errors;
import processorworkflow.Logger;
import processorworkflow.ProcessingBuilder;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class SubcomponentProcessing extends AbstractProcessing<SubcomponentSpec, State> {

    /**
     * Build all extractors first, then build all builders, because
     * we want to gather all addsTo that can be related to other annoted auto subcomponents
     */
    private final Set<SubcomponentExtractor> extractors = new HashSet<>();

    public SubcomponentProcessing(Elements elements, Types types, Errors errors, State state) {
        super(elements, types, errors, state);
    }

    @Override
    public Set<Class<? extends Annotation>> supportedAnnotations() {
        Set set = ImmutableSet.of(AutoSubcomponent.class);
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
        SubcomponentExtractor extractor = new SubcomponentExtractor(element, types, elements, errors);
        if (errors.hasErrors()) {
            return false;
        }

        extractors.add(extractor);
        state.addSubcomponentExtractor(extractor);
        return true;
    }

    private void processExtractors() {
        for (SubcomponentExtractor extractor : extractors) {
            SubcomponentSpec spec = new Builder(extractor, errors).build();
            if (errors.hasErrors()) {
                return;
            }

            specs.add(spec);
        }
    }

    @Override
    public AbstractComposer<SubcomponentSpec> createComposer() {
        return new SubcomponentComposer(specs);
    }

    private class Builder extends ProcessingBuilder<SubcomponentExtractor, SubcomponentSpec> {

        public Builder(SubcomponentExtractor extractor, Errors errors) {
            super(extractor, errors);
        }

        @Override
        protected SubcomponentSpec build() {
            SubcomponentSpec subcomponentSpec = new SubcomponentSpec(AutoComponentClassNameUtil.getComponentClassName(extractor.getElement()));

            if (extractor.getScopeAnnotationTypeMirror() != null) {
                subcomponentSpec.setScopeAnnotationSpec(AnnotationSpec.get(extractor.getScopeAnnotationTypeMirror()));
            }

            // addsTo
            subcomponentSpec.setAddsToTypeNames(ProcessingUtil.getTypeNames(extractor.getAddsToTypeMirrors()));

            // modules
            subcomponentSpec.setModulesTypeNames(ProcessingUtil.getTypeNames(extractor.getModulesTypeMirrors()));

            // superinterfaces
            subcomponentSpec.setSuperinterfacesTypeNames(ProcessingUtil.getTypeNames(extractor.getSuperinterfacesTypeMirrors()));

            // exposed
            subcomponentSpec.setExposeSpecs(ProcessingUtil.getAdditions(extractor.getElement(), state.getExposeExtractors()));

            // injector
            subcomponentSpec.setInjectorSpecs(ProcessingUtil.getAdditions(extractor.getElement(), state.getInjectorExtractors()));

            return subcomponentSpec;
        }
    }
}