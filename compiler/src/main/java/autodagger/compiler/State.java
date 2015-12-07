package autodagger.compiler;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import processorworkflow.AbstractState;
import processorworkflow.Logger;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class State extends AbstractState {

    private final Map<Element, AdditionExtractor> injectorExtractors = new HashMap<>();
    private final Map<Element, AdditionExtractor> exposeExtractors = new HashMap<>();
    private final Map<TypeMirror, List<SubcomponentExtractor>> subcomponentExtractors = new HashMap<>();

    public void addInjectorExtractor(AdditionExtractor extractor) {
        if (injectorExtractors.containsKey(extractor.getElement())) {
            return;
        }

        injectorExtractors.put(extractor.getElement(), extractor);
    }

    public void addExposeExtractor(AdditionExtractor extractor) {
        if (exposeExtractors.containsKey(extractor.getElement())) {
            return;
        }

        exposeExtractors.put(extractor.getElement(), extractor);
    }

    public void addSubcomponentExtractor(SubcomponentExtractor extractor) {
        for (TypeMirror typeMirror : extractor.getAddsToTypeMirrors()) {
            addSubcomponentExtractorToComponent(extractor, typeMirror);
        }
    }

    private void addSubcomponentExtractorToComponent(SubcomponentExtractor extractor, TypeMirror componentTypeMirror) {
        List<SubcomponentExtractor> extractors;
        if (subcomponentExtractors.containsKey(componentTypeMirror)) {
            extractors = subcomponentExtractors.get(componentTypeMirror);
        } else {
            extractors = new ArrayList<>();
            subcomponentExtractors.put(componentTypeMirror, extractors);
        }

        extractors.add(extractor);
    }

    public ImmutableList<AdditionExtractor> getInjectorExtractors() {
        return ImmutableList.copyOf(injectorExtractors.values());
    }

    public ImmutableList<AdditionExtractor> getExposeExtractors() {
        return ImmutableList.copyOf(exposeExtractors.values());
    }

    public ImmutableList<SubcomponentExtractor> getSubcomponentExtractors(TypeMirror typeMirror) {
        if (!subcomponentExtractors.containsKey(typeMirror)) {
            return null;
        }

        return ImmutableList.copyOf(subcomponentExtractors.get(typeMirror));
    }
}