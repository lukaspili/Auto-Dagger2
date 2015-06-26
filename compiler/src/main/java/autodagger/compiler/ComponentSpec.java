package autodagger.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ComponentSpec {

    private final ClassName className;
    private TypeName targetTypeName;
    private AnnotationSpec scopeAnnotationSpec;
    private List<AdditionSpec> injectorSpecs;
    private List<AdditionSpec> exposeSpecs;
    private List<TypeName> dependenciesTypeNames;
    private List<TypeName> modulesTypeNames;
    private List<TypeName> superinterfacesTypeNames;

    public ComponentSpec(ClassName className) {
        this.className = className;
    }

    public ClassName getClassName() {
        return className;
    }

    public TypeName getTargetTypeName() {
        return targetTypeName;
    }

    public void setTargetTypeName(TypeName targetTypeName) {
        this.targetTypeName = targetTypeName;
    }

    public AnnotationSpec getScopeAnnotationSpec() {
        return scopeAnnotationSpec;
    }

    public void setScopeAnnotationSpec(AnnotationSpec scopeAnnotationSpec) {
        this.scopeAnnotationSpec = scopeAnnotationSpec;
    }

    public List<AdditionSpec> getInjectorSpecs() {
        return injectorSpecs;
    }

    public void setInjectorSpecs(List<AdditionSpec> injectorSpecs) {
        this.injectorSpecs = injectorSpecs;
    }

    public List<AdditionSpec> getExposeSpecs() {
        return exposeSpecs;
    }

    public void setExposeSpecs(List<AdditionSpec> exposeSpecs) {
        this.exposeSpecs = exposeSpecs;
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
}
