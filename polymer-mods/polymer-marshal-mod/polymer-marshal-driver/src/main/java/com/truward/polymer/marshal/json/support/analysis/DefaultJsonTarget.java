package com.truward.polymer.marshal.json.support.analysis;

import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.freezable.FreezableSupport;
import com.truward.polymer.marshal.json.analysis.JsonTarget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexander Shabanov
 */
public final class DefaultJsonTarget extends FreezableSupport implements JsonTarget {
  private final GenDomainClass domainClass;
  private GenClass targetReaderClass;
  private GenClass targetWriterClass;
  private GenClass targetMarshallerClass;

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
  public void setTargetReaderClass(@Nonnull GenClass readerClass) {
    checkNonFrozen();
    this.targetReaderClass = readerClass;
  }

  @Nullable
  @Override
  public GenClass getTargetReaderClass() {
    return targetReaderClass;
  }

  @Override
  public void setTargetWriterClass(@Nonnull GenClass targetWriterClass) {
    this.targetWriterClass = targetWriterClass;
  }

  @Nullable
  @Override
  public GenClass getTargetWriterClass() {
    return targetWriterClass;
  }

  @Override
  public void setTargetMarshallerClass(@Nonnull GenClass targetMarshallerClass) {
    this.targetMarshallerClass = targetMarshallerClass;
  }

  @Nullable
  @Override
  public GenClass getTargetMarshallerClass() {
    return targetMarshallerClass;
  }
}
