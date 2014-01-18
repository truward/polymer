package com.truward.polymer.domain.analysis.support;

import com.truward.polymer.core.naming.FqName;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainImplementationTarget;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public class DefaultDomainImplementationTarget implements DomainImplementationTarget {
  private final DomainAnalysisResult analysisResult;
  private FqName targetName;

  public DefaultDomainImplementationTarget(@Nonnull DomainAnalysisResult analysisResult) {
    this.analysisResult = analysisResult;
  }

  @Nonnull
  @Override
  public DomainAnalysisResult getAnalysisResult() {
    return analysisResult;
  }

  @Override
  public void setTargetName(@Nonnull FqName targetName) {
    if (this.targetName != null) {
      throw new IllegalStateException("Target name set twice");
    }
    this.targetName = targetName;
  }

  @Nonnull
  @Override
  public FqName getTargetName() {
    if (targetName == null) {
      throw new IllegalStateException("Target name was not set");
    }
    return targetName;
  }
}
