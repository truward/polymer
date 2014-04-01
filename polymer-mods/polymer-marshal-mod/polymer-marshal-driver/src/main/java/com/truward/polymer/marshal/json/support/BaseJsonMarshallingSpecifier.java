package com.truward.polymer.marshal.json.support;

import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainImplementationTargetSink;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.json.JsonMarshallingSpecifier;
import com.truward.polymer.marshal.json.analysis.JsonMarshallerImplementer;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public abstract class BaseJsonMarshallingSpecifier implements JsonMarshallingSpecifier {

  @Resource
  private DomainImplementationTargetSink implementationTargetSink;

  @Resource
  private DomainAnalysisContext analysisContext;

  protected abstract JsonMarshallerImplementer getImplementer();

  @Override
  public JsonMarshallingSpecifier setGeneratorTarget(@Nonnull FqName targetMethod) {
    return this;
  }

  @Override
  public JsonMarshallingSpecifier addDomainEntity(@Nonnull Class<?> entityClass) {
    // should be reentrant-safe
    final GenDomainClass domainClass = implementationTargetSink.getTarget(analysisContext.analyze(entityClass));
    if (domainClass == null) {
      throw new IllegalStateException("Can't generate gson target for class that has no implementation target: " +
          entityClass);
    }
    getImplementer().submit(domainClass);
    return this;
  }
}
