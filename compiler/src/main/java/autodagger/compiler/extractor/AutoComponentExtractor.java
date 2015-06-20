package autodagger.compiler.extractor;

import com.google.auto.common.MoreElements;

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
import autodagger.compiler.message.Message;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class AutoComponentExtractor {

    static final String ANNOTATION_DEPENDENCIES = "dependencies";
    static final String ANNOTATION_MODULES = "modules";
    static final String ANNOTATION_TARGET = "target";
    static final String ANNOTATION_SUPERINTERFACES = "superinterfaces";

    private final Element element;
    private final TypeMirror targetTypeMirror;
    private final List<TypeMirror> dependenciesTypeMirrors;
    private final List<TypeMirror> modulesTypeMirrors;
    private final List<TypeMirror> superinterfacesTypeMirrors;
    private final AnnotationMirror scopeAnnotationTypeMirror;

    private List<Message> messages = new ArrayList<>();
    private boolean errors;

    public AutoComponentExtractor(Element targetElement,Element autoComponentElement, Types types, Elements elements) {
        this.element = targetElement;

        TypeMirror typeMirror = ExtractorUtils.getValueFromAnnotation(autoComponentElement, AutoComponent.class, ANNOTATION_TARGET);
        if (typeMirror == null) {
            typeMirror = targetElement.asType();
        }
        targetTypeMirror = typeMirror;

        dependenciesTypeMirrors = findTypeMirrors(autoComponentElement, ANNOTATION_DEPENDENCIES);
        modulesTypeMirrors = findTypeMirrors(autoComponentElement, ANNOTATION_MODULES);
        superinterfacesTypeMirrors = findTypeMirrors(autoComponentElement, ANNOTATION_SUPERINTERFACES);
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
                TypeMirror tm = (TypeMirror) value.getValue();
                typeMirrors.add(tm);
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
        AnnotationMirror annotationTypeMirror = null;

        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            Element annotationElement = annotationMirror.getAnnotationType().asElement();
            if (MoreElements.isAnnotationPresent(annotationElement, Scope.class)) {
                // already found one scope
                if (annotationTypeMirror != null) {
                    errors = true;
                    messages.add(Message.error(element, "Class annotated with @AutoComponent cannot have several scopes (@Scope)."));
                    continue;
                }

                annotationTypeMirror = annotationMirror;
            }
        }

        return annotationTypeMirror;
    }

    private boolean validateAnnotationValue(AnnotationValue value, String member) {
        if (!(value.getValue() instanceof TypeMirror)) {
            messages.add(Message.error(element, "%s cannot reference generated class. Use the class that applies the @AutoComponent annotation.", member));
            errors = true;
            return false;
        }

        return true;
    }

    public TypeMirror getTargetTypeMirror() {
        return targetTypeMirror;
    }

    public List<TypeMirror> getModulesTypeMirrors() {
        return modulesTypeMirrors;
    }

    public List<TypeMirror> getDependenciesTypeMirrors() {
        return dependenciesTypeMirrors;
    }

    public List<TypeMirror> getSuperinterfacesTypeMirrors() {
        return superinterfacesTypeMirrors;
    }

    public Element getElement() {
        return element;
    }

    public AnnotationMirror getScopeAnnotationTypeMirror() {
        return scopeAnnotationTypeMirror;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public boolean isErrors() {
        return errors;
    }
}
