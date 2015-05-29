package autodagger.compiler.processingstep;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;

import autodagger.AutoExpose;
import autodagger.compiler.extractor.AutoExposedExtractor;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ExposedProcessingStep implements ProcessingStep {

    private final ProcessingStepBus processingStepBus;

    public ExposedProcessingStep(ProcessingStepBus processingStepBus) {
        this.processingStepBus = processingStepBus;
    }

    @Override
    public Class<? extends Annotation> annotation() {
        return AutoExpose.class;
    }

    @Override
    public void process(Set<? extends Element> elements) {
        for (Element element : elements) {
            AutoExposedExtractor extractor = new AutoExposedExtractor(element);
            processingStepBus.addExposeExtractor(extractor);
        }
    }
}
