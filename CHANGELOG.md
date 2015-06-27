# Change Log

## AutoDagger2 1.1 *(put date)*

 * Big rewrite of internal API, better stability, performances and error handling. Use processor-workflow under the hood.
 * New: `@AutoComponent(fromTemplate = ...)` to apply @AutoComponent from a template
 * New: Apply `@AutoComponent` on your custom annotation, and then apply that custom annotation on the target classes
 * New: `@AutoExpose(parameterizedTypes = ...)` and `@AutoInjector(parameterizedTypes = ...)` to expose and inject objects with parameterized types.
 * Fix: Several small bugs


## AutoDagger2 1.0 

 * Initial release