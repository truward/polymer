package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * Contains the result of analyzing certain class.
 *
 * @author Alexander Shabanov
 */
public interface DomainAnalysisResult {
  @Nonnull
  Class<?> getOriginClass();

  @Nonnull
  Collection<? extends DomainField> getDeclaredFields();

  @Nonnull
  Collection<DomainAnalysisResult> getParents();

  boolean isStub();
}
