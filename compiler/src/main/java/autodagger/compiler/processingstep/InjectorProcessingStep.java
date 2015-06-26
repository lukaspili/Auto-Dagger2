//package autodagger.compiler.processingstep;
//
//import com.google.auto.common.MoreElements;
//
//import java.lang.annotation.Annotation;
//import java.util.Set;
//
//import javax.lang.model.element.Element;
//import javax.lang.model.element.ElementKind;
//
//import autodagger.AutoInjector;
//import autodagger.compiler.extractor.AutoInjectorExtractor;
//import autodagger.compiler.message.MessageDelivery;
//
///**
// * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
// */
//public class InjectorProcessingStep extends ProcessingStep {
//
//    private final ProcessingStepBus processingStepBus;
//    private final MessageDelivery messageDelivery;
//
//    public InjectorProcessingStep(ProcessingStepBus processingStepBus, MessageDelivery messageDelivery) {
//        this.processingStepBus = processingStepBus;
//        this.messageDelivery = messageDelivery;
//    }
//
//    @Override
//    public Class<? extends Annotation> annotation() {
//        return AutoInjector.class;
//    }
//
//    @Override
//    public void process(Set<? extends Element> elements) {
//        for (Element element : elements) {
//            if (ElementKind.ANNOTATION_TYPE.equals(element.getKind())) {
//                Set<? extends Element> targetElements = roundEnv.getElementsAnnotatedWith(MoreElements.asType(element));
//                for (Element targetElement : targetElements) {
//                    addAutoInjectorExtractor(targetElement, element);
//                }
//                continue;
//            }
//            addAutoInjectorExtractor(element, element);
//        }
//    }
//
//    private void addAutoInjectorExtractor(Element targetElement, Element autoInjectorElement) {
//        AutoInjectorExtractor extractor = new AutoInjectorExtractor(targetElement, autoInjectorElement);
//        processingStepBus.addInjectorExtractor(extractor);
//    }
//}
