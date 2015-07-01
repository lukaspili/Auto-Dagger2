# Change Log

## AutoDagger2 1.1 *06/30/15*

 * Big rewrite of internal API, better stability, performances and error handling. Use processor-workflow under the hood
 * New: Change maven dependency groupId from `com.github.lukaspili` to **`com.github.lukaspili.autodagger2`**
 * New: `@AutoComponent(includes = ...)` to include @AutoComponent from another annotation
 * New: Apply `@AutoComponent` on your custom annotation, and then apply that custom annotation on the target classes (thanks ricechen)
 * New: `@AutoExpose(parameterizedTypes = ...)` and `@AutoInjector(parameterizedTypes = ...)` to expose and inject objects with parameterized types
 * Fix: `@AutoExpose` in provider methods does not loose the qualifier annotation
 * Fix: Several small bugs
 * Fix: The dependency does not contain the Dagger2 dependency anymore

 * BREAKING CHANGE: `@AutoExpose` in provider methods creates the associated method in the generated component that has the same name than the provider method, without the "provides". Example: `providesMyObject()` will produce `myObject()`


## AutoDagger2 1.0 

 * Initial release