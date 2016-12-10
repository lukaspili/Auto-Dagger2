package autodagger.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class SubcomponentSpec {

    private final ClassName className;
    private AnnotationSpec scopeAnnotationSpec;
    private List<AdditionSpec> injectorSpecs;
    private List<AdditionSpec> exposeSpecs;
    private List<TypeName> modulesTypeNames;
    private List<TypeName> superinterfacesTypeNames;
    private List<MethodSpec> subcomponentsSpecs;

    public SubcomponentSpec(ClassName className) {
        this.className = className;
    }

    public ClassName getClassName() {
        return className;
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

    public List<MethodSpec> getSubcomponentsSpecs() {
        return subcomponentsSpecs;
    }

    public void setSubcomponentsSpecs(List<MethodSpec> subcomponentsSpecs) {
        this.subcomponentsSpecs = subcomponentsSpecs;
    }
}