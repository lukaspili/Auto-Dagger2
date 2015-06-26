package autodagger.compiler;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import processorworkflow.AbstractState;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class State extends AbstractState {

    private final Map<Element, AdditionExtractor> injectorExtractors = new HashMap<>();
    private final Map<Element, AdditionExtractor> exposeExtractors = new HashMap<>();
    private final Set<TypeMirror> targets = new HashSet<>();

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

    public ImmutableList<AdditionExtractor> getInjectorExtractors() {
        return ImmutableList.copyOf(injectorExtractors.values());
    }

    public ImmutableList<AdditionExtractor> getExposeExtractors() {
        return ImmutableList.copyOf(exposeExtractors.values());
    }

    public Set<TypeMirror> getTargets() {
        return targets;
    }
}
