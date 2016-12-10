package autodagger.compiler;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Scope;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.AutoSubcomponent;
import autodagger.compiler.utils.AutoComponentExtractorUtil;
import dagger.Subcomponent;
import processorworkflow.AbstractExtractor;
import processorworkflow.Errors;
import processorworkflow.ExtractorUtils;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class SubcomponentExtractor extends AbstractExtractor {

    private List<TypeMirror> modulesTypeMirrors;
    private List<TypeMirror> superinterfacesTypeMirrors;
    private List<TypeMirror> subcomponentsTypeMirrors;
    private AnnotationMirror scopeAnnotationTypeMirror;

    public SubcomponentExtractor(Element element, Types types, Elements elements, Errors errors) {
        super(element, types, elements, errors);

        extract();
    }

    @Override
    public void extract() {
        modulesTypeMirrors = findTypeMirrors(element, AutoComponentExtractorUtil.ANNOTATION_MODULES);
        subcomponentsTypeMirrors = findTypeMirrors(element, AutoComponentExtractorUtil.ANNOTATION_SUBCOMPONENTS);
        if (!MoreElements.isAnnotationPresent(element, AutoSubcomponent.class)) {
            return;
        }

        superinterfacesTypeMirrors = findTypeMirrors(element, AutoComponentExtractorUtil.ANNOTATION_SUPERINTERFACES);
        scopeAnnotationTypeMirror = findScope();
    }

    private List<TypeMirror> findTypeMirrors(Element element, String name) {
        boolean addsTo = name.equals(AutoComponentExtractorUtil.ANNOTATION_SUBCOMPONENTS);

        List<TypeMirror> typeMirrors = new ArrayList<>();
        List<AnnotationValue> values = ExtractorUtils.getValueFromAnnotation(element, AutoSubcomponent.class, name);
        if (values != null) {
            for (AnnotationValue value : values) {
                if (!validateAnnotationValue(value, name)) {
                    continue;
                }

                try {
                    TypeMirror tm = (TypeMirror) value.getValue();
                    if (addsTo) {
                        Element e = MoreTypes.asElement(tm);
                        if (!MoreElements.isAnnotationPresent(e, AutoSubcomponent.class) && !MoreElements.isAnnotationPresent(e, Subcomponent.class)) {
                            errors.addInvalid("@AutoComponent cannot declare a subcomponent that is not annotated with @Subcomponent or @AutoSubcomponent: %s", e.getSimpleName());
                            continue;
                        }
                    }
                    typeMirrors.add(tm);
                } catch (Exception e) {
                    errors.addInvalid(e.getMessage());
                    break;
                }
            }
        }

        return typeMirrors;
    }

    /**
     * Find annotation that is itself annoted with @Scope
     * If there is one, it will be later applied on the generated component
     * Otherwise the component will be unscoped
     * Throw error if more than one scope annotation found
     */
    private AnnotationMirror findScope() {
        List<AnnotationMirror> annotationMirrors = ExtractorUtil.findAnnotatedAnnotation(element, Scope.class);
        if (annotationMirrors.isEmpty()) {
            return null;
        }

        if (annotationMirrors.size() > 1) {
            errors.getParent().addInvalid(element, "Cannot have several scope (@Scope).");
            return null;
        }

        return annotationMirrors.get(0);
    }

    private boolean validateAnnotationValue(AnnotationValue value, String member) {
        if (!(value.getValue() instanceof TypeMirror)) {
            errors.addInvalid("%s cannot reference generated class. Use the class that applies the @AutoComponent annotation", member);
            return false;
        }

        return true;
    }

    public List<TypeMirror> getModulesTypeMirrors() {
        return modulesTypeMirrors;
    }

    public AnnotationMirror getScopeAnnotationTypeMirror() {
        return scopeAnnotationTypeMirror;
    }

    public List<TypeMirror> getSubcomponentsTypeMirrors() {
        return subcomponentsTypeMirrors;
    }

    public List<TypeMirror> getSuperinterfacesTypeMirrors() {
        return superinterfacesTypeMirrors;
    }
}
