package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class ImplementationTarget {
  private DomainAnalysisResult source;
  private String className;

  public ImplementationTarget(@Nonnull DomainAnalysisResult source, @Nonnull String className) {
    this.source = source;
    this.className = className;
  }

  @Nonnull
  public DomainAnalysisResult getSource() {
    return source;
  }

  @Nonnull
  public String getClassName() {
    return className;
  }
}
