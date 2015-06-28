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

import autodagger.AutoComponent;
import processorworkflow.AbstractExtractor;
import processorworkflow.Errors;
import processorworkflow.ExtractorUtils;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ComponentExtractor extends AbstractExtractor {

    static final String ANNOTATION_DEPENDENCIES = "dependencies";
    static final String ANNOTATION_MODULES = "modules";
    static final String ANNOTATION_TARGET = "target";
    static final String ANNOTATION_SUPERINTERFACES = "superinterfaces";
    static final String ANNOTATION_FROM_TEMPLATE = "fromTemplate";

    /**
     * The component element represented by @AutoComponent
     * It's either the element itself, or the element of an annotation if the @AutoComponent
     * is applied on the annotation
     */
    private final Element componentElement;

    private TypeMirror targetTypeMirror;
    private List<TypeMirror> dependenciesTypeMirrors;
    private List<TypeMirror> modulesTypeMirrors;
    private List<TypeMirror> superinterfacesTypeMirrors;
    private AnnotationMirror scopeAnnotationTypeMirror;

    public ComponentExtractor(Element componentElement, Element element, Types types, Elements elements, Errors errors) {
        super(element, types, elements, errors);
        this.componentElement = componentElement;

        extract();
    }

    @Override
    public void extract() {
        targetTypeMirror = ExtractorUtils.getValueFromAnnotation(element, AutoComponent.class, ANNOTATION_TARGET);
        if (targetTypeMirror == null) {
            targetTypeMirror = componentElement.asType();
        }

        ComponentExtractor templateExtractor = null;
        TypeMirror fromTemplateTypeMirror = ExtractorUtils.getValueFromAnnotation(element, AutoComponent.class, ANNOTATION_FROM_TEMPLATE);
        if (fromTemplateTypeMirror != null) {
            Element templateElement = MoreTypes.asElement(fromTemplateTypeMirror);
            if (!MoreElements.isAnnotationPresent(templateElement, AutoComponent.class)) {
                errors.getParent().addInvalid(templateElement, "Template must be annotated with @AutoComponent");
                return;
            }

            templateExtractor = new ComponentExtractor(templateElement, templateElement, types, elements, errors.getParent());
            if (errors.getParent().hasErrors()) {
                return;
            }
        }

        if (templateExtractor == null) {
            dependenciesTypeMirrors = findTypeMirrors(element, ANNOTATION_DEPENDENCIES);
            modulesTypeMirrors = findTypeMirrors(element, ANNOTATION_MODULES);
            superinterfacesTypeMirrors = findTypeMirrors(element, ANNOTATION_SUPERINTERFACES);
        } else {
            dependenciesTypeMirrors = templateExtractor.getDependenciesTypeMirrors();
            modulesTypeMirrors = templateExtractor.getModulesTypeMirrors();
            superinterfacesTypeMirrors = templateExtractor.getSuperinterfacesTypeMirrors();
        }

        scopeAnnotationTypeMirror = findScope();
    }

    private List<TypeMirror> findTypeMirrors(Element element, String name) {
        List<TypeMirror> typeMirrors = new ArrayList<>();
        List<AnnotationValue> values = ExtractorUtils.getValueFromAnnotation(element, AutoComponent.class, name);
        if (values != null) {
            for (AnnotationValue value : values) {
                if (!validateAnnotationValue(value, name)) {
                    continue;
                }

                try {
                    TypeMirror tm = (TypeMirror) value.getValue();
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
        // first look on the @AutoComponent annotated element
        AnnotationMirror annotationMirror = findScope(element);
        if (annotationMirror == null && element != componentElement) {
            // look also on the real component element, if @AutoComponent is itself on
            // an another annotation
            annotationMirror = findScope(componentElement);
        }

        return annotationMirror;
    }

    private AnnotationMirror findScope(Element element) {
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
            errors.addInvalid(String.format("%s cannot reference generated class. Use the class that applies the @AutoComponent annotation.", member));
            return false;
        }

        return true;
    }

    public Element getComponentElement() {
        return componentElement;
    }

    public TypeMirror getTargetTypeMirror() {
        return targetTypeMirror;
    }

    public List<TypeMirror> getDependenciesTypeMirrors() {
        return dependenciesTypeMirrors;
    }

    public List<TypeMirror> getModulesTypeMirrors() {
        return modulesTypeMirrors;
    }

    public List<TypeMirror> getSuperinterfacesTypeMirrors() {
        return superinterfacesTypeMirrors;
    }

    public AnnotationMirror getScopeAnnotationTypeMirror() {
        return scopeAnnotationTypeMirror;
    }
}
