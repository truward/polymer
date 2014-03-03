package com.truward.polymer.marshal.gson.support.analysis;

import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.gson.analysis.GsonField;
import com.truward.polymer.marshal.gson.analysis.GsonTarget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class DefaultGsonTarget implements GsonTarget {
  private final GenDomainClass domainClass;

  public DefaultGsonTarget(@Nonnull GenDomainClass domainClass) {
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
}
