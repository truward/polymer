package com.truward.polymer.marshal.gson.specification.support;

import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.marshal.gson.GsonMarshallingSpecifier;
import com.truward.polymer.marshal.gson.analysis.GsonTarget;
import com.truward.polymer.marshal.gson.implementer.GsonMarshallerImplementer;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class DefaultGsonMarshallingSpecifier implements GsonMarshallingSpecifier {

  @Resource
  private DomainAnalysisContext analysisContext;

  @Resource
  private GsonMarshallerImplementer implementer;

  private Map<Class<?>, GsonTarget> targetMap = new HashMap<>();

  @Override
  public GsonMarshallingSpecifier setGeneratorTarget(@Nonnull FqName targetMethod) {
    return this;
  }

  @Override
  public GsonMarshallingSpecifier addDomainEntity(@Nonnull Class<?> entityClass) {
    if (!targetMap.containsKey(entityClass)) {
      final GsonTarget gsonTarget = new GsonTarget(analysisContext.analyze(entityClass));
      implementer.submit(gsonTarget);
      targetMap.put(entityClass, gsonTarget);
    }
    return this;
  }
}
