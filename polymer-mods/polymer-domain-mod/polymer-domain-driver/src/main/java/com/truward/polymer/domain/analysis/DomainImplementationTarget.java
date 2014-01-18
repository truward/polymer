package com.truward.polymer.domain.analysis;

import com.truward.polymer.core.naming.FqName;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface DomainImplementationTarget {

  @Nonnull
  DomainAnalysisResult getAnalysisResult();

  void setTargetName(@Nonnull FqName targetName);

  @Nonnull
  FqName getTargetName();
}
