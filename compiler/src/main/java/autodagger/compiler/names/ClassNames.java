package autodagger.compiler.names;

import com.google.auto.common.MoreElements;
import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;

/**
 * All the logic about class names for a specific element in one place
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ClassNames {

    private final String elementPackage;
    private final ClassName componentClassName;

    public ClassNames(Element element) {
        Preconditions.checkNotNull(element);

        elementPackage = MoreElements.getPackage(element).getQualifiedName().toString();
        componentClassName = ClassName.get(elementPackage, ClassNames.buildComponentName(element));
    }

    public static String buildComponentName(Element element) {
        String name = element.getSimpleName().toString();
        return String.format("%sComponent", name);
    }

    public ClassName getComponentClassName() {
        return componentClassName;
    }
}
