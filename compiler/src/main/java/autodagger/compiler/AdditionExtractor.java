package autodagger.compiler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import processorworkflow.AbstractExtractor;
import processorworkflow.Errors;
import processorworkflow.ExtractorUtils;
import processorworkflow.Logger;

/**
 * Extracts @AutoInjector and @AutoExpose
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class AdditionExtractor extends AbstractExtractor {

    /**
     * The addition element represented by @AutoInjector or @AutoExpose
     * It's either the element itself, or the element of an annotation if the @AutoXXX
     * is applied on the annotation
     */
    private final Element additionElement;
    private final Class<? extends Annotation> additionAnnotation;

    private TypeMirror additionTypeMirror;
    private List<TypeMirror> targetTypeMirrors = new ArrayList<>();

    public AdditionExtractor(Element additionElement, Class<? extends Annotation> additionAnnotation, Element element, Types types, Elements elements, Errors errors) {
        super(element, types, elements, errors);
        this.additionElement = additionElement;
        this.additionAnnotation = additionAnnotation;

        extract();
    }

    @Override
    public void extract() {
        additionTypeMirror = additionElement.asType();

        List<AnnotationValue> values = ExtractorUtils.getValueFromAnnotation(element, additionAnnotation, "value");
        if (values != null && !values.isEmpty()) {
            for (AnnotationValue value : values) {
                try {
                    TypeMirror tm = (TypeMirror) value.getValue();
                    targetTypeMirrors.add(tm);
                    Logger.d("Add target TM %s", tm);
                } catch (Exception e) {
                    errors.addInvalid(e.getMessage());
                    return;
                }
            }
        } else {
            // if there's no value, the target is the element itself
            targetTypeMirrors.add(additionElement.asType());
        }
    }

    public TypeMirror getAdditionTypeMirror() {
        return additionTypeMirror;
    }

    public List<TypeMirror> getTargetTypeMirrors() {
        return targetTypeMirrors;
    }
}
