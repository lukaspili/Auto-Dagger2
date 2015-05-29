package autodagger.compiler.model.spec;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ComponentSpec extends AbstractSpec {

    private Element element;
    private TypeName targetTypeName;
    private List<InjectorSpec> injectorSpecs;
    private List<ExposedSpec> exposedSpecs;
    private List<TypeName> dependenciesTypeNames;
    private List<TypeName> modulesTypeNames;
    private List<TypeName> superinterfacesTypeNames;
    private AnnotationMirror scopeAnnotationMirror;

    public ComponentSpec(ClassName className) {
        super(className);
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public TypeName getTargetTypeName() {
        return targetTypeName;
    }

    public void setTargetTypeName(TypeName targetTypeName) {
        this.targetTypeName = targetTypeName;
    }

    public List<InjectorSpec> getInjectorSpecs() {
        return injectorSpecs;
    }

    public void setInjectorSpecs(List<InjectorSpec> injectorSpecs) {
        this.injectorSpecs = injectorSpecs;
    }

    public List<ExposedSpec> getExposedSpecs() {
        return exposedSpecs;
    }

    public void setExposedSpecs(List<ExposedSpec> exposedSpecs) {
        this.exposedSpecs = exposedSpecs;
    }

    public List<TypeName> getDependenciesTypeNames() {
        return dependenciesTypeNames;
    }

    public void setDependenciesTypeNames(List<TypeName> dependenciesTypeNames) {
        this.dependenciesTypeNames = dependenciesTypeNames;
    }

    public List<TypeName> getModulesTypeNames() {
        return modulesTypeNames;
    }

    public void setModulesTypeNames(List<TypeName> modulesTypeNames) {
        this.modulesTypeNames = modulesTypeNames;
    }

    public List<TypeName> getSuperinterfacesTypeNames() {
        return superinterfacesTypeNames;
    }

    public void setSuperinterfacesTypeNames(List<TypeName> superinterfacesTypeNames) {
        this.superinterfacesTypeNames = superinterfacesTypeNames;
    }

    public AnnotationMirror getScopeAnnotationMirror() {
        return scopeAnnotationMirror;
    }

    public void setScopeAnnotationMirror(AnnotationMirror scopeAnnotationMirror) {
        this.scopeAnnotationMirror = scopeAnnotationMirror;
    }
}
