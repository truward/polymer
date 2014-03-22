package com.truward.polymer.marshal.json.support.analysis;

import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.json.analysis.JsonTarget;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class DefaultJsonTarget extends FreezableSupport implements JsonTarget {
  private final GenDomainClass domainClass;

  public DefaultJsonTarget(@Nonnull GenDomainClass domainClass) {
    this.domainClass = domainClass;
  }

  @Override
  @Nonnull
  public GenDomainClass getDomainClass() {
    return domainClass;
  }

  @Override
  @Nonnull
  public DomainAnalysisResult getDomainAnalysisResult() {
    return domainClass.getOrigin();
  }

  @Override
  public boolean isReaderSupportRequested() {
    return true;
  }

  @Override
  public boolean isWriterSupportRequested() {
    return true;
  }

  @Override
  protected void setFrozen() {
  }
}
