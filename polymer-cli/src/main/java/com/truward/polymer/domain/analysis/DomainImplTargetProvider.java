package com.truward.polymer.domain.analysis;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface DomainImplTargetProvider {

  @Nonnull
  List<DomainImplTarget> getImplementationTargets();
}
