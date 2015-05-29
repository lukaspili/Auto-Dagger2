package autodagger.compiler.extractor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import autodagger.AutoInjector;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class AutoInjectorExtractor {

    private final Element element;
    private final List<TypeMirror> typeMirrors;

    public AutoInjectorExtractor(Element element) {
        this.element = element;

        typeMirrors = new ArrayList<>();
        List<AnnotationValue> values = ExtractorUtils.getValueFromAnnotation(element, AutoInjector.class, "value");
        if (values != null) {
            for (AnnotationValue value : values) {
                TypeMirror tm = (TypeMirror) value.getValue();
                typeMirrors.add(tm);
            }
        }
    }

    public Element getElement() {
        return element;
    }

    public List<TypeMirror> getTypeMirrors() {
        return typeMirrors;
    }
}
