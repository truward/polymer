package com.truward.polymer.domain.analysis.support;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.typesystem.NamingUtil;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainAnalysisResult implements DomainAnalysisResult {
  private final Class<?> originClass;
  private final List<DomainAnalysisResult> parents;
  private final List<DomainField> declaredFields;

  public DefaultDomainAnalysisResult(@Nonnull Class<?> clazz,
                                     @Nonnull List<DomainAnalysisResult> parents,
                                     @Nonnull List<DomainField> declaredFields) {
    this.originClass = clazz;
    this.parents = ImmutableList.copyOf(parents);
    this.declaredFields = ImmutableList.copyOf(declaredFields);
  }

  @Nonnull
  @Override
  public Class<?> getOriginClass() {
    return originClass;
  }

  @Nonnull
  @Override
  public Collection<? extends DomainField> getDeclaredFields() {
    return declaredFields;
  }

  @Nonnull
  @Override
  public Collection<DomainAnalysisResult> getParents() {
    return parents;
  }

  @Override
  public boolean isStub() {
    return false;
  }
}
