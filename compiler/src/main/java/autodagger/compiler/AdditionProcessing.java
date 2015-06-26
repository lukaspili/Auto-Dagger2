package autodagger.compiler;

import com.google.auto.common.MoreElements;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.AutoExpose;
import autodagger.AutoInjector;
import processorworkflow.AbstractComposer;
import processorworkflow.AbstractProcessing;
import processorworkflow.Errors;

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
        AdditionSpec.Type type;
        boolean hasInjector = MoreElements.isAnnotationPresent(element, AutoInjector.class);
        boolean hasExpose = MoreElements.isAnnotationPresent(element, AutoExpose.class);
        if (hasInjector && hasExpose) {
            type = AdditionSpec.Type.BOTH;
        } else {
            type = hasInjector ? AdditionSpec.Type.INJECTOR : AdditionSpec.Type.EXPOSE;
        }

        if (ElementKind.ANNOTATION_TYPE.equals(element.getKind())) {
            // @AutoInjector is applied on another annotation, find out the targets of that annotation
            Set<? extends Element> targetElements = roundEnvironment.getElementsAnnotatedWith(MoreElements.asType(element));
            for (Element targetElement : targetElements) {
                if (!process(targetElement, element, type)) {
                    return false;
                }
            }
        }

        process(element, element, type);
        return !errors.hasErrors();
    }

    private boolean process(Element targetElement, Element element, AdditionSpec.Type type) {
        AdditionExtractor extractor = new AdditionExtractor(targetElement, element, types, elements, errors);
        if (errors.hasErrors()) {
            return false;
        }

        Preconditions.checkArgument(!extractor.getTargetTypeMirrors().isEmpty(), "Addition target cannot be empty");

        if (type == AdditionSpec.Type.BOTH) {
            state.addInjectorExtractor(extractor);
            state.addExposeExtractor(extractor);
        } else {
            if (type == AdditionSpec.Type.INJECTOR) {
                state.addInjectorExtractor(extractor);
            } else {
                state.addExposeExtractor(extractor);
            }
        }

        return true;
    }

    @Override
    public AbstractComposer<AdditionSpec> createComposer() {
        return null;
    }
}
