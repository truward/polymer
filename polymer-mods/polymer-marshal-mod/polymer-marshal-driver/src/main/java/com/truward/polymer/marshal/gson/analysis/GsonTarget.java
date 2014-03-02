package com.truward.polymer.marshal.gson.analysis;

import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.support.GenDomainClass;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class GsonTarget {
  private final GenDomainClass domainClass;

  public GsonTarget(@Nonnull GenDomainClass domainClass) {
    this.domainClass = domainClass;
  }

  public GenDomainClass getDomainClass() {
    return domainClass;
  }

  public DomainAnalysisResult getAnalysisResult() {
    return domainClass.getOrigin();
  }
}
