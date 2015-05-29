package autodagger.compiler.processingstep;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import autodagger.compiler.extractor.AutoComponentExtractor;
import autodagger.compiler.extractor.AutoExposedExtractor;
import autodagger.compiler.extractor.AutoInjectorExtractor;

/**
 * Pass data between processing steps
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ProcessingStepBus {

    private final Map<Element, AutoInjectorExtractor> autoInjectorExtractors = new HashMap<>();
    private final Map<Element, AutoExposedExtractor> autoExposedExtractors = new HashMap<>();
    private final Map<TypeMirror, Element> componentTargets = new HashMap<>();

    public void addInjectorExtractor(AutoInjectorExtractor extractor) {
        if (autoInjectorExtractors.containsKey(extractor.getElement())) {
            return;
        }

        autoInjectorExtractors.put(extractor.getElement(), extractor);
    }

    public void addExposeExtractor(AutoExposedExtractor extractor) {
        if (autoExposedExtractors.containsKey(extractor.getElement())) {
            return;
        }

        autoExposedExtractors.put(extractor.getElement(), extractor);
    }

    public ImmutableList<AutoInjectorExtractor> getInjectorExtractors() {
        return ImmutableList.copyOf(autoInjectorExtractors.values());
    }

    public ImmutableList<AutoExposedExtractor> getExposedExtractors() {
        return ImmutableList.copyOf(autoExposedExtractors.values());
    }

    public Map<TypeMirror, Element> getComponentTargets() {
        return componentTargets;
    }
}
