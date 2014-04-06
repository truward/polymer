TODOs
=====

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
