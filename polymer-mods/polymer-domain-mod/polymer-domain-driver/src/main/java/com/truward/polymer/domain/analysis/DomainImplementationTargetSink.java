package com.truward.polymer.domain.analysis;

import com.truward.polymer.domain.analysis.support.GenDomainClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexander Shabanov
 */
public interface DomainImplementationTargetSink {

  @Nonnull
  GenDomainClass submit(@Nonnull DomainAnalysisResult analysisResult);

  @Nullable
  GenDomainClass getTarget(@Nonnull DomainAnalysisResult analysisResult);
}
