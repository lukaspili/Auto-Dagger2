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

        if (!StringUtils.endsWith(name, "Component")) {
            name = name + "Component";
        }

        return ClassName.get(pkg, name);
    }
}
