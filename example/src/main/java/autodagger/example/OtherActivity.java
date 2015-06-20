package autodagger.example;

import android.app.Activity;
import android.os.Bundle;

import java.lang.reflect.AccessibleObject;

import autodagger.AutoInjector;

@AutoActivityComponent
@ScopeActivity
@AutoInjector
public class OtherActivity extends Activity {

    private OtherActivityComponent component;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
