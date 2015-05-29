package autodagger.compiler.message;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class Message {

    private final Diagnostic.Kind kind;
    private final Element element;
    private final String text;

    public static Message note(Element element, String text, Object... format) {
        return new Message(Diagnostic.Kind.NOTE, element, text, format);
    }

    public static Message warning(Element element, String text, Object... format) {
        return new Message(Diagnostic.Kind.WARNING, element, text, format);
    }

    public static Message error(Element element, String text, Object... format) {
        return new Message(Diagnostic.Kind.ERROR, element, text, format);
    }

    public Message(Diagnostic.Kind kind, Element element, String text) {
        this.kind = kind;
        this.element = element;
        this.text = text;
    }

    public Message(Diagnostic.Kind kind, Element element, String text, Object... format) {
        this(kind, element, format.length > 0 ? String.format(text, format) : text);
    }

    public Element getElement() {
        return element;
    }

    public String getText() {
        return text;
    }

    public Diagnostic.Kind getKind() {
        return kind;
    }
}
