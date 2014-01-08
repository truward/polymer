package com.truward.polymer.domain.analysis.support;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.trait.TraitContainerSupport;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainAnalysisResult extends TraitContainerSupport implements DomainAnalysisResult {
  private final Class<?> originClass;
  private final List<DomainAnalysisResult> parents;
  private final List<DomainField> declaredFields;
  private final List<DomainField> fields;

  public DefaultDomainAnalysisResult(@Nonnull Class<?> clazz,
                                     @Nonnull List<DomainAnalysisResult> parents,
                                     @Nonnull List<DomainField> declaredFields,
                                     @Nonnull List<DomainField> fields) {
    this.originClass = clazz;
    this.parents = ImmutableList.copyOf(parents);
    this.declaredFields = ImmutableList.copyOf(declaredFields);
    this.fields = ImmutableList.copyOf(fields);
  }

  @Nonnull
  @Override
  public Class<?> getOriginClass() {
    return originClass;
  }

  @Nonnull
  @Override
  public List<DomainField> getDeclaredFields() {
    return declaredFields;
  }

  @Nonnull
  @Override
  public List<DomainAnalysisResult> getParents() {
    return parents;
  }

  @Nonnull
  @Override
  public List<DomainField> getFields() {
    return fields;
  }

  @Override
  public boolean isStub() {
    return false;
  }
}
