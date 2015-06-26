package autodagger.compiler;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.AutoExpose;
import autodagger.AutoInjector;
import dagger.Provides;
import processorworkflow.AbstractComposer;
import processorworkflow.AbstractProcessing;
import processorworkflow.Errors;
import processorworkflow.Logger;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class AdditionProcessing extends AbstractProcessing<AdditionSpec, State> {

    public AdditionProcessing(Elements elements, Types types, Errors errors, State state) {
        super(elements, types, errors, state);
    }

    @Override
    public Set<Class<? extends Annotation>> supportedAnnotations() {
        return ImmutableSet.of(AutoInjector.class, AutoExpose.class);
    }

    @Override
    public boolean processElement(Element element, Errors.ElementErrors elementErrors) {
        // @AutoX applied on annotation
        if (element.getKind() == ElementKind.ANNOTATION_TYPE) {
            // @AutoX is applied on another annotation, find out the targets of that annotation
            Set<? extends Element> targetElements = roundEnvironment.getElementsAnnotatedWith(MoreElements.asType(element));
            for (Element targetElement : targetElements) {
                if (!process(targetElement, element)) {
                    return false;
                }
            }
            return true;
        }

        // @AutoX applied on method
        // only valid for @AutoExpose with @Provides
        if (element.getKind() == ElementKind.METHOD) {
            if (processedAnnotation.equals(AutoInjector.class)) {
                errors.addInvalid(element, "@AutoInjector cannot be applied on a method");
                return false;
            }

            if (!MoreElements.isAnnotationPresent(element, Provides.class)) {
                errors.addInvalid(element, "@AutoExpose can be applied on @Provides method only");
                return false;
            }

            ExecutableElement executableElement = MoreElements.asExecutable(element);
            Element returnElement = MoreTypes.asElement(executableElement.getReturnType());

            return process(returnElement, element);
        }

        process(element, element);
        return !errors.hasErrors();
    }

    private boolean process(Element targetElement, Element element) {
        Logger.d("Process %s - %s", targetElement.getSimpleName(), element.getSimpleName());

        AdditionExtractor extractor = new AdditionExtractor(targetElement, processedAnnotation, element, types, elements, errors);
        if (errors.hasErrors()) {
            return false;
        }

        Preconditions.checkArgument(!extractor.getTargetTypeMirrors().isEmpty(), "Addition target cannot be empty");

        if (processedAnnotation.equals(AutoInjector.class)) {
            Logger.d("Add to Injectors");
            state.addInjectorExtractor(extractor);
        } else {
            Logger.d("Add to Exposes");
            state.addExposeExtractor(extractor);
        }

        return true;
    }

    @Override
    public AbstractComposer<AdditionSpec> createComposer() {
        return null;
    }
}
