package com.truward.polymer.domain.analysis.support;

import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainAnalysisContext implements DomainAnalysisContext {
  @Override
  @Nonnull
  public DomainAnalysisResult analyze(@Nonnull Class<?> clazz) {
    if (!clazz.isInterface()) {
      throw new IllegalArgumentException("Only interfaces are expected, got " + clazz);
    }

    return new DefaultDomainAnalysisResult(clazz);
  }
}
