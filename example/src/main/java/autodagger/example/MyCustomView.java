package autodagger.example;

import android.content.Context;
import android.widget.LinearLayout;

import autodagger.AutoInjector;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoInjector(FirstActivity.class)
public class MyCustomView extends LinearLayout {

    public MyCustomView(Context context) {
        super(context);
    }
}
