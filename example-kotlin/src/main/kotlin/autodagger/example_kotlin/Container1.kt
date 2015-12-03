package autodagger.example_kotlin

import autodagger.AutoComponent

/**
 * Created by lukasz on 03/12/15.
 */
@AutoComponent(dependencies = arrayOf(KotlinMainActivity::class))
@DaggerScope(Container1::class)
class Container1 {
}