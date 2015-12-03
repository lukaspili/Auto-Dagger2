package autodagger.example_kotlin

import android.app.Activity
import android.os.Bundle
import autodagger.AutoComponent

/**
 * Created by lukasz on 02/12/15.
 */
@AutoComponent(dependencies = arrayOf(KotlinExampleApplication::class))
@DaggerScope(KotlinMainActivity::class)
class KotlinMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}