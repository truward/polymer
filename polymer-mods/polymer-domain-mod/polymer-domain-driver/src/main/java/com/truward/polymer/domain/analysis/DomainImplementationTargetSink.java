package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface DomainImplementationTargetSink {

  void submit(@Nonnull DomainAnalysisResult analysisResult);
}
