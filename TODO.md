TODOs
=====

* Implement builder support
* Implement optional Guava support
* Make it possible to opt-out from the ImmutableList support
* Implement target name specifier:
   implementationTargets.add(new DomainImplementationTarget(currentAnalysisResult,
        FqName.parse("com.mysite." + currentAnalysisResult.getOriginClass().getName() + "Impl")));
* Freeze analysis settings after specification phase

## Minor

* Use WeakPtr string in the FqName to avoid recalculation of toString() - probably doesn't make any sense

## Postponed

* Use Jco in favor to JavaCodeGenerator
* Use FqName where possible + FqNamePool to minimize creation of the fq names
