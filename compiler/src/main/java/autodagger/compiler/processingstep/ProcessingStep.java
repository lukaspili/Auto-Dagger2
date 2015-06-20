package autodagger.compiler.processingstep;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public abstract class ProcessingStep {

    protected RoundEnvironment roundEnv;
    protected ProcessingEnvironment processingEnvironment;

    public final void process(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        this.processingEnvironment = processingEnv;
        this.roundEnv = roundEnv;
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation());
        process(elements);
    }

    public abstract Class<? extends Annotation> annotation();

    public abstract void process(Set<? extends Element> elements);
}
