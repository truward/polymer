package com.truward.polymer.marshal.gson.support.analysis;

import com.truward.polymer.core.code.typed.GenEmergentClass;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.gson.analysis.GenTypeAdapterClass;
import com.truward.polymer.marshal.gson.analysis.GsonTarget;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class DefaultGsonTarget extends FreezableSupport implements GsonTarget {
  private final GenDomainClass domainClass;
  private final GenTypeAdapterClass typeAdapterClass;

  public DefaultGsonTarget(@Nonnull GenDomainClass domainClass) {
    this.domainClass = domainClass;
    this.typeAdapterClass = new DefaultGenTypeAdapterClass();
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

  @Nonnull
  @Override
  public GenTypeAdapterClass getTypeAdapter() {
    return typeAdapterClass;
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
    typeAdapterClass.freeze();
  }

  private static final class DefaultGenTypeAdapterClass extends GenEmergentClass implements GenTypeAdapterClass {
  }
}
