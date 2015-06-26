package autodagger.compiler;

import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.AutoComponent;
import processorworkflow.AbstractComposer;
import processorworkflow.AbstractProcessing;
import processorworkflow.Errors;

/**
 * Find all targets for @AutoComponent
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class TargetsProcessing extends AbstractProcessing<Void, State> {

    public TargetsProcessing(Elements elements, Types types, Errors errors, State state) {
        super(elements, types, errors, state);
    }

    @Override
    public Set<Class<? extends Annotation>> supportedAnnotations() {
        Set set = ImmutableSet.of(AutoComponent.class);
        return set;
    }

    @Override
    public boolean processElement(Element element, Errors.ElementErrors elementErrors) {
        if (ElementKind.ANNOTATION_TYPE.equals(element.getKind())) {
            // @AutoComponent is applied on another annotation, find out the targets of that annotation
            Set<? extends Element> targetElements = roundEnvironment.getElementsAnnotatedWith(MoreElements.asType(element));
            for (Element targetElement : targetElements) {
                process(targetElement);
                if (errors.hasErrors()) {
                    return false;
                }
            }
            return true;
        }

        process(element);

        if (errors.hasErrors()) {
            return false;
        }

        return true;
    }

    private void process(Element targetElement) {
        state.getTargets().add(targetElement.asType());
    }

    @Override
    public AbstractComposer<Void> createComposer() {
        return null;
    }
}
