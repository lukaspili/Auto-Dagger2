package autodagger.compiler;

import com.google.auto.service.AutoService;

import java.util.LinkedList;

import javax.annotation.processing.Processor;

import processorworkflow.AbstractProcessing;
import processorworkflow.AbstractProcessor;
import processorworkflow.Logger;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor<State> {

    @Override
    protected State processingState() {
        return new State();
    }

    @Override
    protected LinkedList<AbstractProcessing> processings() {
        LinkedList<AbstractProcessing> processings = new LinkedList<>();
        processings.add(new TargetsProcessing(elements, types, errors, state));
        processings.add(new AdditionProcessing(elements, types, errors, state));
        processings.add(new ComponentProcessing(elements, types, errors, state));
        return processings;
    }

    public AnnotationProcessor() {
        super();

        // don't forget to disable logging before releasing
        // find a way to have the boolean set automatically via gradle
        Logger.init("AutoDagger2 Processor", false);
    }

}
