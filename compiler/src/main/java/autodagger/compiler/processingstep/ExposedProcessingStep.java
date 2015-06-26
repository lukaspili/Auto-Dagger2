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
//import autodagger.AutoExpose;
//import autodagger.compiler.extractor.AutoExposedExtractor;
//
///**
// * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
// */
//public class ExposedProcessingStep extends ProcessingStep {
//
//    private final ProcessingStepBus processingStepBus;
//
//    public ExposedProcessingStep(ProcessingStepBus processingStepBus) {
//        this.processingStepBus = processingStepBus;
//    }
//
//    @Override
//    public Class<? extends Annotation> annotation() {
//        return AutoExpose.class;
//    }
//
//    @Override
//    public void process(Set<? extends Element> elements) {
//        for (Element element : elements) {
//            if (ElementKind.ANNOTATION_TYPE.equals(element.getKind())) {
//                Set<? extends Element> targetElements = roundEnv.getElementsAnnotatedWith(MoreElements.asType(element));
//                for (Element targetElement : targetElements) {
//                    addAutoExposeExtractor(targetElement, element);
//                }
//                continue;
//            }
//            addAutoExposeExtractor(element, element);
//        }
//    }
//
//    private void addAutoExposeExtractor(Element targetElement, Element autoExposeElement) {
//        AutoExposedExtractor extractor = new AutoExposedExtractor(targetElement, autoExposeElement);
//        processingStepBus.addExposeExtractor(extractor);
//    }
//}
