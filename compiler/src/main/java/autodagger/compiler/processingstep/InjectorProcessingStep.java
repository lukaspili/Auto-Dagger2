package autodagger.compiler.processingstep;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;

import autodagger.AutoInjector;
import autodagger.compiler.extractor.AutoInjectorExtractor;
import autodagger.compiler.message.MessageDelivery;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class InjectorProcessingStep implements ProcessingStep {

    private final ProcessingStepBus processingStepBus;
    private final MessageDelivery messageDelivery;

    public InjectorProcessingStep(ProcessingStepBus processingStepBus, MessageDelivery messageDelivery) {
        this.processingStepBus = processingStepBus;
        this.messageDelivery = messageDelivery;
    }

    @Override
    public Class<? extends Annotation> annotation() {
        return AutoInjector.class;
    }

    @Override
    public void process(Set<? extends Element> elements) {
        for (Element element : elements) {
            AutoInjectorExtractor extractor = new AutoInjectorExtractor(element);
            processingStepBus.addInjectorExtractor(extractor);
        }
    }
}
