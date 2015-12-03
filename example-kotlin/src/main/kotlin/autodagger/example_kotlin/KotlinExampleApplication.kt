package autodagger.example_kotlin

import android.app.Application
import autodagger.AutoComponent

/**
 * Created by lukasz on 02/12/15.
 */
@AutoComponent
@DaggerScope(KotlinExampleApplication::class)
class KotlinExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()


    }
}