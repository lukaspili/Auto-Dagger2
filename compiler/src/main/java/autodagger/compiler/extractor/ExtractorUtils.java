//package autodagger.compiler.extractor;
//
//import com.google.auto.common.MoreElements;
//import com.google.auto.common.MoreTypes;
//import com.google.common.base.Optional;
//
//import java.lang.annotation.Annotation;
//import java.util.Map;
//
//import javax.lang.model.element.AnnotationMirror;
//import javax.lang.model.element.AnnotationValue;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.ExecutableElement;
//import javax.lang.model.type.DeclaredType;
//import javax.lang.model.util.Elements;
//import javax.lang.model.util.Types;
//
///**
// * Mix of own code and from stackoverflow.com
// *
// * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
// */
//final class ExtractorUtils {
//
//    private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
//        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
//            if (entry.getKey().getSimpleName().toString().equals(key)) {
//                return entry.getValue();
//            }
//        }
//        return null;
//    }
//
//    public static <T> T getValueFromAnnotation(Element element, Class<? extends Annotation> annotation, String name) {
//        Optional<AnnotationMirror> annotationMirror = MoreElements.getAnnotationMirror(element, annotation);
//        if (!annotationMirror.isPresent()) {
//            return null;
//        }
//
//        AnnotationValue annotationValue = getAnnotationValue(annotationMirror.get(), name);
//        return annotationValue != null ? (T) annotationValue.getValue() : null;
//    }
//
//    public static Optional<DeclaredType> getSuperclassDeclaredType(Types types, Elements elements, Element element) {
//        try {
//            return MoreTypes.nonObjectSuperclass(types, elements, MoreTypes.asDeclared(element.asType()));
//        } catch (Exception e) {
//            return Optional.absent();
//        }
//    }
//}
