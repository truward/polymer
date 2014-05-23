TODOs
=====

* Split interface package with the real 'driver' packages - this will allow to avoid unwanted dependency in code on the
real implementation. Driver will look like as follows:
    driver-specification-api (visible to user),
    driver-plugin-api (visible to other driver implementations, allows other drivers to operate with this driver),
    driver-impl (runtime dep).
    REMOVE: polymer-core=>api/impl, polymer-mods=>polymer-plugins
Also split polymer-core into polymer-core-api and polymer-core-impl
* Compiler Friendly CLI option - don't overwrite existing identical files (so compiler won't recompile them)
* Compiler Friendly CLI option - remove non-generated (older) files from generated sources dir
* Freeze analysis settings after specification phase

* Code DOM
* Transient/Calculated Fields for the domain objects
* Constructor generator (reuse object construction)

# Done
* Implement builder support
* Make it possible to opt-out from the ImmutableList support
* Implement optional Guava support
* Implement target name specifier:
   implementationTargets.add(new DomainImplementationTarget(currentAnalysisResult,
        FqName.parse("com.mysite." + currentAnalysisResult.getOriginClass().getName() + "Impl")));

## Minor

* Use WeakPtr string in the FqName to avoid recalculation of toString() - probably doesn't make any sense

## Postponed

* Use Jco in favor to JavaCodeGenerator
* Use FqName where possible + FqNamePool to minimize creation of the fq names
