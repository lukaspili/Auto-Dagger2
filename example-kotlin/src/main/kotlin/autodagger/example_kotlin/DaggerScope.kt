package autodagger.example_kotlin

import javax.inject.Scope
import kotlin.reflect.KClass

/**
 * Created by lukasz on 02/12/15.
 */
@Scope
annotation class DaggerScope(val value: KClass<*>)