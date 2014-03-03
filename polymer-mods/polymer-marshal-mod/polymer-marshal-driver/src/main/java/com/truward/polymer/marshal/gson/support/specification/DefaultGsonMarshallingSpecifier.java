package com.truward.polymer.marshal.gson.support.specification;

import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainImplementationTargetSink;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.gson.GsonMarshallingSpecifier;
import com.truward.polymer.marshal.gson.analysis.GsonMarshallerImplementer;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public final class DefaultGsonMarshallingSpecifier implements GsonMarshallingSpecifier {

  @Resource
  private DomainImplementationTargetSink implementationTargetSink;

  @Resource
  private DomainAnalysisContext analysisContext;

  @Resource
  private GsonMarshallerImplementer implementer;

  @Override
  public GsonMarshallingSpecifier setGeneratorTarget(@Nonnull FqName targetMethod) {
    return this;
  }

  @Override
  public GsonMarshallingSpecifier addDomainEntity(@Nonnull Class<?> entityClass) {
    // should be reentrant-safe
    final GenDomainClass domainClass = implementationTargetSink.getTarget(analysisContext.analyze(entityClass));
    if (domainClass == null) {
      throw new IllegalStateException("Can't generate gson target for class that has no implementation target: " +
          entityClass);
    }
    implementer.submit(domainClass);
    return this;
  }
}
