package autodagger.compiler.utils;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.ClassName;

import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class AutoComponentClassNameUtil {

    public static ClassName getComponentClassName(Element element) {
        String pkg = MoreElements.getPackage(element).getQualifiedName().toString();
        String name = element.getSimpleName().toString();

        return ClassName.get(pkg, getComponentSimpleName(name));
    }

    public static ClassName getComponentClassName(ClassName elementClassName) {
        return ClassName.get(elementClassName.packageName(), getComponentSimpleName(elementClassName.simpleName()));
    }

    public static String getComponentSimpleName(String elementName) {
        if (!StringUtils.endsWith(elementName, "Component")) {
            return elementName + "Component";
        }

        return elementName;
    }
}
