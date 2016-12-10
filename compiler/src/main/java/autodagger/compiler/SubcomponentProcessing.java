package autodagger.compiler;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.AutoSubcomponent;
import autodagger.compiler.utils.AutoComponentClassNameUtil;
import dagger.Subcomponent;
import processorworkflow.AbstractComposer;
import processorworkflow.AbstractProcessing;
import processorworkflow.Errors;
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
        Set set = ImmutableSet.of(AutoSubcomponent.class, Subcomponent.class);
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

        if (!extractor.getModulesTypeMirrors().isEmpty()) {
            state.addSubcomponentModule(element.asType(), extractor.getModulesTypeMirrors());
        }

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

            // modules
            subcomponentSpec.setModulesTypeNames(ProcessingUtil.getTypeNames(extractor.getModulesTypeMirrors()));

            // superinterfaces
            subcomponentSpec.setSuperinterfacesTypeNames(ProcessingUtil.getTypeNames(extractor.getSuperinterfacesTypeMirrors()));

            // exposed
            subcomponentSpec.setExposeSpecs(ProcessingUtil.getAdditions(extractor.getElement(), state.getExposeExtractors()));

            // injector
            subcomponentSpec.setInjectorSpecs(ProcessingUtil.getAdditions(extractor.getElement(), state.getInjectorExtractors()));

            // subcomponents
            subcomponentSpec.setSubcomponentsSpecs(getSubcomponents());

            return subcomponentSpec;
        }

        private List<MethodSpec> getSubcomponents() {
            if (extractor.getSubcomponentsTypeMirrors().isEmpty()) {
                return Collections.emptyList();
            }

            List<MethodSpec> methodSpecs = new ArrayList<>(extractor.getSubcomponentsTypeMirrors().size());
            for (TypeMirror typeMirror : extractor.getSubcomponentsTypeMirrors()) {
                Element e = MoreTypes.asElement(typeMirror);
                TypeName typeName;
                String name;
                if (MoreElements.isAnnotationPresent(e, AutoSubcomponent.class)) {
                    ClassName cls = AutoComponentClassNameUtil.getComponentClassName(e);
                    typeName = cls;
                    name = cls.simpleName();
                } else {
                    typeName = TypeName.get(typeMirror);
                    name = e.getSimpleName().toString();
                }

                List<TypeMirror> modules = state.getSubcomponentModules(typeMirror);
                List<ParameterSpec> parameterSpecs;
                if(modules != null) {
                    parameterSpecs = new ArrayList<>(modules.size());
                    int count = 0;
                    for (TypeMirror moduleTypeMirror : modules) {
                        parameterSpecs.add(ParameterSpec.builder(TypeName.get(moduleTypeMirror), String.format("module%d", ++count)).build());
                    }
                } else {
                    parameterSpecs = new ArrayList<>(0);
                }

                methodSpecs.add(MethodSpec.methodBuilder("plus" + name)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameters(parameterSpecs)
                        .returns(typeName)
                        .build());
            }

            return methodSpecs;
        }
    }

}