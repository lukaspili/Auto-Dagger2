package autodagger.example;

import android.content.Context;
import android.widget.LinearLayout;

import autodagger.AutoInjector;

/**
 * Showcase: add injector method in some generated component
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoInjector(FirstActivity.class)
public class MyCustomView extends LinearLayout {

    public MyCustomView(Context context) {
        super(context);
    }
}
