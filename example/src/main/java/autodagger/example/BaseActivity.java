package autodagger.example;

import android.app.Activity;
import android.os.Bundle;

import autodagger.AutoComponent;
import autodagger.AutoExpose;
import autodagger.AutoInjector;
import dagger.Provides;

@AutoActivityComponent
@AutoInjector
@ScopeActivity
public class BaseActivity extends Activity {

    private BaseActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExampleApplicationComponent parentComponent = ((ExampleApplication) getApplication()).getComponent();
        component = DaggerBaseActivityComponent.builder()
                .exampleApplicationComponent(parentComponent)
                .build();
    }

    @dagger.Module
    public static class Module {
    }
}
