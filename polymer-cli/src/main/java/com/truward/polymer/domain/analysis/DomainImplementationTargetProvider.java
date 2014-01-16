package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface DomainImplementationTargetProvider {

  void submit(@Nonnull DomainAnalysisResult analysisResult);

  @Nonnull
  List<DomainImplementationTarget> getImplementationTargets();
}
