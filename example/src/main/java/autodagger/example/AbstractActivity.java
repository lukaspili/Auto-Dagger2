package autodagger.example;

import android.app.Activity;
import android.os.Bundle;

import autodagger.AutoInjector;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@StandardActivityComponent1
@AutoInjector
@DaggerScope(AbstractActivity.class)
public abstract class AbstractActivity extends Activity {

    protected AbstractActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = DaggerAbstractActivityComponent.builder()
                .myAppComponent(((MyApp) getApplication()).getComponent())
                .build();
        component.inject(this);
    }
}
