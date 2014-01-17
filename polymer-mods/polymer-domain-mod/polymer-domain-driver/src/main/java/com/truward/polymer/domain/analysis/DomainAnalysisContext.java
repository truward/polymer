package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface DomainAnalysisContext {
  @Nonnull
  DomainAnalysisResult analyze(@Nonnull Class<?> clazz);
}
