package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Tries to reinterpret incoming class as a domain object, extracts information about its
 * fields and field names.
 *
 * @author Alexander Shabanov
 */
public interface DomainAnalysisResult {
  @Nonnull
  Class<?> getOriginClass();

  @Nonnull
  Collection<? extends DomainField> getDeclaredFields();
}
