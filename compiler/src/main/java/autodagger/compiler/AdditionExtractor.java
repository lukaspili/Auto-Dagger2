package autodagger.compiler;

import com.google.auto.common.MoreElements;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import processorworkflow.AbstractExtractor;
import processorworkflow.Errors;
import processorworkflow.ExtractorUtils;

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
    private TypeElement additionElement;
    private final Class<? extends Annotation> additionAnnotation;

    private List<TypeMirror> targetTypeMirrors;
    private List<TypeMirror> parameterizedTypeMirrors;

    public AdditionExtractor(Element additionElement, Class<? extends Annotation> additionAnnotation, Element element, Types types, Elements elements, Errors errors) {
        super(element, types, elements, errors);
        this.additionAnnotation = additionAnnotation;

        try {
            this.additionElement = MoreElements.asType(additionElement);
        } catch (Exception e) {
            errors.addInvalid(additionElement, "Must be a class");
            return;
        }

        extract();
    }

    @Override
    public void extract() {
        targetTypeMirrors = getTypeMirrors("value");
        if (targetTypeMirrors.isEmpty()) {
            // if there's no value, the target is the element itself
            targetTypeMirrors.add(additionElement.asType());
        }

        parameterizedTypeMirrors = getTypeMirrors("parameterizedTypes");
    }

    private List<TypeMirror> getTypeMirrors(String member) {
        List<AnnotationValue> values = ExtractorUtils.getValueFromAnnotation(element, additionAnnotation, member);
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }

        List<TypeMirror> typeMirrors = new ArrayList<>();
        for (AnnotationValue value : values) {
            try {
                TypeMirror tm = (TypeMirror) value.getValue();
                typeMirrors.add(tm);
            } catch (Exception e) {
                errors.addInvalid(String.format("Cannot extract member %s because %s", member, e.getMessage()));
            }
        }

        return typeMirrors;
    }

    public TypeElement getAdditionElement() {
        return additionElement;
    }

    public List<TypeMirror> getTargetTypeMirrors() {
        return targetTypeMirrors;
    }

    public List<TypeMirror> getParameterizedTypeMirrors() {
        return parameterizedTypeMirrors;
    }
}
