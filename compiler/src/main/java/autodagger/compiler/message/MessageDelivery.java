package autodagger.compiler.message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class MessageDelivery {

    private final List<Message> messages = new LinkedList<>();
    private int warnings;
    private int errors;

    public void add(Message message) {
        if (message.getKind() == Diagnostic.Kind.WARNING) {
            warnings++;
        } else if (message.getKind() == Diagnostic.Kind.ERROR) {
            errors++;
        }

        messages.add(message);
    }

    public void deliver(Messager messager) {
        if (messages.isEmpty()) {
            return;
        }

        for (Message message : messages) {
            messager.printMessage(message.getKind(), message.getText(), message.getElement());
        }
    }

    public boolean hasErrors() {
        return errors > 0;
    }
}
