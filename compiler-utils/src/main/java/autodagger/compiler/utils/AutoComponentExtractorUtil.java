package autodagger.compiler.utils;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import autodagger.AutoComponent;
import processorworkflow.Errors;
import processorworkflow.ExtractorUtils;

/**
 * Some extraction utils exported that can other annotation processor library reuse
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class AutoComponentExtractorUtil {

    public static final String ANNOTATION_DEPENDENCIES = "dependencies";
    public static final String ANNOTATION_MODULES = "modules";
    public static final String ANNOTATION_TARGET = "target";
    public static final String ANNOTATION_SUPERINTERFACES = "superinterfaces";
    public static final String ANNOTATION_INCLUDES = "includes";
    public static final String ANNOTATION_SUBCOMPONENTS = "subcomponents";

    public static List<TypeMirror> getDependencies(AnnotationMirror autoComponentAnnotationMirror, Errors.ElementErrors errors) {
        // get dependency from @AutoComponent
        List<TypeMirror> deps = new ArrayList<>();

        TypeMirror includesTypeMirror = ExtractorUtils.getValueFromAnnotation(autoComponentAnnotationMirror, AutoComponent.class, ANNOTATION_INCLUDES);
        if (includesTypeMirror != null) {
            // includes
            Element includesElement = MoreTypes.asElement(includesTypeMirror);
            if (!MoreElements.isAnnotationPresent(includesElement, AutoComponent.class)) {
                errors.addInvalid("Included element %s is missing @AutoComponent annotation", includesElement.getSimpleName());
                return deps;
            }

            List<AnnotationValue> values = ExtractorUtils.getValueFromAnnotation(includesElement, AutoComponent.class, ANNOTATION_DEPENDENCIES);
            deps.addAll(findTypeMirrors(values, errors));
        }

        List<AnnotationValue> values = ExtractorUtils.getValueFromAnnotation(autoComponentAnnotationMirror, AutoComponent.class, ANNOTATION_DEPENDENCIES);
        deps.addAll(findTypeMirrors(values, errors));

        return deps;
    }

    public static List<TypeMirror> findTypeMirrors(List<AnnotationValue> values, Errors.ElementErrors errors) {
        List<TypeMirror> typeMirrors = new ArrayList<>();
        if (values != null) {
            for (AnnotationValue value : values) {
                try {
                    TypeMirror tm = (TypeMirror) value.getValue();
                    typeMirrors.add(tm);
                } catch (Exception e) {
                    errors.addInvalid("@AutoComponent dependency %s. Did your reference an auto generated class? You must use the target class", value);
                    break;
                }
            }
        }

        return typeMirrors;
    }
}
