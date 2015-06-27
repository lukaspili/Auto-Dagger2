package autodagger.example;

import android.os.Bundle;

/**
 * Showcase: extends from base class annotated with @AutoComponent
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ForthActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // do this here, or in the base class
        component = DaggerAbstractActivityComponent.builder()
                .myAppComponent(((MyApp) getApplication()).getComponent())
                .build();
        component.inject(this);
    }
}
