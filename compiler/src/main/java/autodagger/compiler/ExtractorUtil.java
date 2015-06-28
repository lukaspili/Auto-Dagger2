package autodagger.compiler;

import com.google.auto.common.MoreElements;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
final class ExtractorUtil {

    public static List<AnnotationMirror> findAnnotatedAnnotation(Element element, Class<? extends Annotation> annotationCls) {
        List<AnnotationMirror> annotationMirrors = new ArrayList<>();

        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            Element annotationElement = annotationMirror.getAnnotationType().asElement();
            if (MoreElements.isAnnotationPresent(annotationElement, annotationCls)) {
                annotationMirrors.add(annotationMirror);
            }
        }

        return annotationMirrors;
    }
}
