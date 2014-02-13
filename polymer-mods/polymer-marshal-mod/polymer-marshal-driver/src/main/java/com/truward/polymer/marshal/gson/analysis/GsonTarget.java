package com.truward.polymer.marshal.gson.analysis;

import com.truward.polymer.core.util.TargetTrait;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class GsonTarget {
  private final DomainAnalysisResult result;
  private final TargetTrait targetTrait;

  public GsonTarget(@Nonnull DomainAnalysisResult result) {
    this.result = result;
    final TargetTrait targetTrait = result.findTrait(TargetTrait.KEY);
    if (targetTrait == null) {
      throw new IllegalStateException("Target trait is null for a given class " + result.getOriginClass() + " - " +
          "domain object has no associated implementation, can't generate marshaller code");
    }
    this.targetTrait = targetTrait;
  }
}
