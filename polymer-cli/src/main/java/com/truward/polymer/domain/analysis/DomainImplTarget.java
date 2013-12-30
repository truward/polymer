package com.truward.polymer.domain.analysis;

import com.truward.polymer.code.naming.FqName;

import javax.annotation.Nonnull;

/**
 * Implementation target, which is represents a code entity to be generated.
 *
 * @author Alexander Shabanov
 */
public final class DomainImplTarget {
  private DomainAnalysisResult source;
  private FqName fqName;

  public DomainImplTarget(@Nonnull DomainAnalysisResult source, @Nonnull FqName fqName) {
    this.source = source;
    this.fqName = fqName;
  }

  @Nonnull
  public DomainAnalysisResult getSource() {
    return source;
  }

  @Nonnull
  public FqName getClassName() {
    return fqName;
  }
}
