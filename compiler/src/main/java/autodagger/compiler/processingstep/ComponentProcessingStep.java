package autodagger.compiler.processingstep;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.AutoComponent;
import autodagger.AutoExpose;
import autodagger.AutoInjector;
import autodagger.compiler.MisunderstoodPoet;
import autodagger.compiler.extractor.AutoComponentExtractor;
import autodagger.compiler.extractor.AutoExposedExtractor;
import autodagger.compiler.extractor.AutoInjectorExtractor;
import autodagger.compiler.message.Message;
import autodagger.compiler.message.MessageDelivery;
import autodagger.compiler.model.spec.ComponentSpec;
import autodagger.compiler.model.spec.ExposedSpec;
import autodagger.compiler.model.spec.InjectorSpec;
import autodagger.compiler.names.ClassNames;
import dagger.Provides;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ComponentProcessingStep implements ProcessingStep {

    private final Types types;
    private final Elements elements;
    private final Filer filer;
    private final MessageDelivery messageDelivery;
    private final ProcessingStepBus processingStepBus;
    private final MisunderstoodPoet misunderstoodPoet;

    public ComponentProcessingStep(Types types, Elements elements, Filer filer, MessageDelivery messageDelivery, ProcessingStepBus processingStepBus) {
        Preconditions.checkNotNull(types);
        Preconditions.checkNotNull(elements);
        Preconditions.checkNotNull(filer);
        Preconditions.checkNotNull(messageDelivery);
        Preconditions.checkNotNull(processingStepBus);

        this.types = types;
        this.elements = elements;
        this.filer = filer;
        this.messageDelivery = messageDelivery;
        this.processingStepBus = processingStepBus;

        misunderstoodPoet = new MisunderstoodPoet();
    }

    @Override
    public Class<? extends Annotation> annotation() {
        return AutoComponent.class;
    }

    @Override
    public void process(Set<? extends Element> elements) {
        List<AutoComponentExtractor> extractors = new ArrayList<>();
        for (Element element : elements) {
            AutoComponentExtractor componentExtractor = new AutoComponentExtractor(element, this.types, this.elements);

            boolean valid = validateComponentExtractor(componentExtractor);
            if (!valid) {
                // do not try to build screen for already invalid element
                continue;
            }

            extractors.add(componentExtractor);
            processingStepBus.getComponentTargets().put(componentExtractor.getTargetTypeMirror(), componentExtractor.getElement());
        }

        if (extractors.isEmpty()) {
            return;
        }

        List<ComponentSpec> componentSpecs = new ArrayList<>();
        for (AutoComponentExtractor componentExtractor : extractors) {
            ClassNames classNames = new ClassNames(componentExtractor.getElement());

            ComponentSpec componentSpec = buildComponent(componentExtractor, classNames, processingStepBus.getInjectorExtractors(), processingStepBus.getExposedExtractors(), processingStepBus.getComponentTargets());
            componentSpecs.add(componentSpec);
        }

        boolean valid = validateSpecs(componentSpecs);
        if (valid) {
            generateSpecs(componentSpecs);
        }
    }

    private ComponentSpec buildComponent(AutoComponentExtractor componentExtractor, ClassNames classNames, List<AutoInjectorExtractor> injectorExtractors, List<AutoExposedExtractor> exposedExtractors, Map<TypeMirror, Element> targetsTypeMirrors) {
        Preconditions.checkNotNull(componentExtractor, "Component extractor cannot be null");
        Preconditions.checkNotNull(classNames, "ClassNames cannot be null");
        Preconditions.checkNotNull(injectorExtractors, "Injector extractors cannot be null");
        Preconditions.checkNotNull(exposedExtractors, "Expose extractors cannot be null");
        Preconditions.checkNotNull(targetsTypeMirrors, "Targets type mirrors cannot be null");

        ComponentSpec componentSpec = new ComponentSpec(classNames.getComponentClassName());
        componentSpec.setElement(componentExtractor.getElement());
        componentSpec.setScopeAnnotationMirror(componentExtractor.getScopeAnnotationTypeMirror());

        // injectors
        componentSpec.setInjectorSpecs(buildInjectorSpecs(componentExtractor, injectorExtractors));

        // exposed
        componentSpec.setExposedSpecs(buildExposedSpecs(componentExtractor, exposedExtractors));

        // dependencies
        componentSpec.setDependenciesTypeNames(buildDependenciesTypeNames(componentExtractor.getDependenciesTypeMirrors(), targetsTypeMirrors));

        // superinterfaces
        componentSpec.setSuperinterfacesTypeNames(buildDependenciesTypeNames(componentExtractor.getSuperinterfacesTypeMirrors(), targetsTypeMirrors));

        List<TypeName> modulesTypeNames = new ArrayList<>();
        if (componentExtractor.getModulesTypeMirrors() != null) {
            for (TypeMirror typeMirror : componentExtractor.getModulesTypeMirrors()) {
                modulesTypeNames.add(TypeName.get(typeMirror));
            }
        }
        componentSpec.setModulesTypeNames(modulesTypeNames);

        return componentSpec;
    }

    private List<TypeName> buildDependenciesTypeNames(List<TypeMirror> typeMirrors, Map<TypeMirror, Element> targetsTypeMirrors) {
        List<TypeName> typeNames = new ArrayList<>();
        if (typeMirrors == null) {
            return typeNames;
        }

        loop:
        for (TypeMirror typeMirror : typeMirrors) {
            // check if dependency type mirror references an @AutoComponent target class
            // if so, build the TypeName that matches the target component
            for (Map.Entry<TypeMirror, Element> entry : targetsTypeMirrors.entrySet()) {
                if (ProcessingUtils.compareTypeWithOneOfSeverals(entry.getKey(), typeMirror)) {
                    Element element = entry.getValue();
                    String pkg = MoreElements.getPackage(element).getQualifiedName().toString();
                    ClassName className = ClassName.get(pkg, ClassNames.buildComponentName(element));
                    typeNames.add(className);
                    continue loop;
                }
            }

            typeNames.add(TypeName.get(typeMirror));
        }

        return typeNames;
    }

    private List<InjectorSpec> buildInjectorSpecs(AutoComponentExtractor componentExtractor, List<AutoInjectorExtractor> injectorExtractors) {
        List<InjectorSpec> injectorSpecs = new ArrayList<>();

        for (AutoInjectorExtractor extractor : injectorExtractors) {
            // empty @AutoInjector must be on same target
            // compare on both element and target element in order to show correct error message
            if (extractor.getTypeMirrors().isEmpty() &&
                    ProcessingUtils.compareTypeWithOneOfSeverals(extractor.getElement().asType(), componentExtractor.getTargetTypeMirror(), componentExtractor.getElement().asType())) {
                // applies on target
                if (!validateEmptyAutoAnnotation(componentExtractor, extractor.getElement(), AutoInjector.class)) {
                    continue;
                }
            }

            // without value
            if (extractor.getTypeMirrors().isEmpty() &&
                    ProcessingUtils.compareTypeWithOneOfSeverals(componentExtractor.getTargetTypeMirror(), extractor.getElement().asType())) {
                String name = StringUtils.uncapitalize(extractor.getElement().getSimpleName().toString());
                injectorSpecs.add(new InjectorSpec(name, TypeName.get(extractor.getElement().asType())));
                continue;
            }

            if (!extractor.getTypeMirrors().isEmpty()) {
                for (TypeMirror typeMirror : extractor.getTypeMirrors()) {
                    if (ProcessingUtils.compareTypeWithOneOfSeverals(componentExtractor.getTargetTypeMirror(), typeMirror)) {
                        String name = StringUtils.uncapitalize(extractor.getElement().getSimpleName().toString());
                        injectorSpecs.add(new InjectorSpec(name, TypeName.get(extractor.getElement().asType())));
                    }
                }
            }
        }

        return injectorSpecs;
    }

    private List<ExposedSpec> buildExposedSpecs(AutoComponentExtractor componentExtractor, List<AutoExposedExtractor> exposedExtractors) {
        List<ExposedSpec> exposedSpecs = new ArrayList<>();

        for (AutoExposedExtractor extractor : exposedExtractors) {
            // empty @AutoExpose must be on same target
            // compare on both element and target element in order to show correct error message
            if (extractor.getTypeMirrors().isEmpty() &&
                    ProcessingUtils.compareTypeWithOneOfSeverals(extractor.getElement().asType(), componentExtractor.getTargetTypeMirror(), componentExtractor.getElement().asType())) {
                // applies on target
                if (!validateEmptyAutoAnnotation(componentExtractor, extractor.getElement(), AutoExpose.class)) {
                    continue;
                }
            }

            // @AutoExpose applied on method
            TypeMirror elementTypeMirror = extractor.getElement().asType();
            if (elementTypeMirror.getKind() == TypeKind.EXECUTABLE) {
                // Must be a @Module provider method
                ExecutableElement executableElement = MoreElements.asExecutable(extractor.getElement());

                if (!MoreElements.isAnnotationPresent(executableElement, Provides.class)) {
                    messageDelivery.add(Message.error(executableElement, "@AutoExpose can only be applied on type or on dagger module provider method"));
                    continue;
                }

                Element returnElement = MoreTypes.asElement(executableElement.getReturnType());
                String name = StringUtils.uncapitalize(returnElement.getSimpleName().toString());
                ExposedSpec exposedSpec = new ExposedSpec(name, TypeName.get(returnElement.asType()));

                for (TypeMirror typeMirror : extractor.getTypeMirrors()) {
                    if (ProcessingUtils.compareTypeWithOneOfSeverals(componentExtractor.getTargetTypeMirror(), typeMirror)) {
                        exposedSpecs.add(exposedSpec);
                    }
                }

                continue;
            }

            // without value
            if (extractor.getTypeMirrors().isEmpty() &&
                    ProcessingUtils.compareTypeWithOneOfSeverals(componentExtractor.getTargetTypeMirror(), extractor.getElement().asType())) {
                String name = StringUtils.uncapitalize(extractor.getElement().getSimpleName().toString());
                exposedSpecs.add(new ExposedSpec(name, TypeName.get(extractor.getElement().asType())));
                continue;
            }

            for (TypeMirror typeMirror : extractor.getTypeMirrors()) {
                if (ProcessingUtils.compareTypeWithOneOfSeverals(componentExtractor.getTargetTypeMirror(), typeMirror)) {
                    String name = StringUtils.uncapitalize(extractor.getElement().getSimpleName().toString());
                    exposedSpecs.add(new ExposedSpec(name, TypeName.get(extractor.getElement().asType())));
                }
            }
        }

        return exposedSpecs;
    }

    /**
     * AutoInjector or AutoExpose without value
     * Only on the target element of @AutoComponent
     */

    private boolean validateEmptyAutoAnnotation(AutoComponentExtractor componentExtractor, Element element, Class<? extends Annotation> cls) {
        // element must be a type
        if (element.getKind() == ElementKind.CLASS) {
            Element targetElement = MoreTypes.asElement(componentExtractor.getTargetTypeMirror());
            TypeElement targetTypeElement = MoreElements.asType(targetElement);

            // the element on which the @AutoInjector or @AutoExpose is applied
            TypeElement typeElement = MoreElements.asType(element);

            // element is not applied on target
            if (targetTypeElement.getQualifiedName().equals(typeElement.getQualifiedName())) {
                return true;
            }
        }

        messageDelivery.add(Message.error(element, "@%s without value can only be used on the @AutoComponent target (which is by default the class that has the @AutoComponent annotation)", cls.getSimpleName()));
        return false;
    }

    private boolean validateComponentExtractor(AutoComponentExtractor componentExtractor) {
        Preconditions.checkNotNull(componentExtractor);

        if (componentExtractor.isErrors()) {
            for (Message message : componentExtractor.getMessages()) {
                messageDelivery.add(message);
            }

            return false;
        }

        return true;
    }

    private boolean validateSpecs(List<ComponentSpec> componentSpecs) {
        // TODO: add validation
        return true;
    }

    private void generateSpecs(List<ComponentSpec> componentSpecs) {
        for (ComponentSpec componentSpec : componentSpecs) {
            TypeSpec typeSpec = misunderstoodPoet.compose(componentSpec);
            JavaFile javaFile = JavaFile.builder(componentSpec.getClassName().packageName(), typeSpec).build();
            write(javaFile, componentSpec.getElement());
        }
    }

    private void write(JavaFile javaFile, Element element) {
        try {
            javaFile.writeTo(filer);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            messageDelivery.add(Message.error(element, "Unable to generate class for %s. %s", javaFile.typeSpec.name, stackTrace));
        }
    }
}
