package autodagger.example_kotlin

import autodagger.AutoComponent

/**
 * Created by lukasz on 03/12/15.
 */
@AutoComponent(includes = MainActivityChildComponent::class)
@DaggerScope(Container2::class)
class Container2 {
}