package autodagger.example;

import android.app.Activity;
import android.os.Bundle;

import autodagger.AutoComponent;
import autodagger.AutoInjector;

/**
 * Showcase: @AutoComponent includes
 *
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoComponent(
        modules = SixthActivity.Module.class,
        includes = StandardActivityComponent1.class)
@DaggerScope(SixthActivity.class)
@AutoInjector
public class SixthActivity extends Activity {

    private SixthActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = DaggerSixthActivityComponent.builder()
                .myAppComponent(((MyApp) getApplication()).getComponent())
                .build();
        component.inject(this);
    }

    @dagger.Module
    public static class Module {

    }
}