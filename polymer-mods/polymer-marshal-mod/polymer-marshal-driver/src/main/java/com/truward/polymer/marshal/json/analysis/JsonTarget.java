package com.truward.polymer.marshal.json.analysis;

import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.support.GenDomainClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexander Shabanov
 */
public interface JsonTarget extends Freezable {
  @Nonnull
  DomainAnalysisResult getDomainAnalysisResult();

  @Nonnull
  GenDomainClass getDomainClass();

  boolean isReaderSupportRequested();

  boolean isWriterSupportRequested();

  void setTargetReaderClass(@Nonnull GenClass readerClass);

  @Nullable
  GenClass getTargetReaderClass();

  void setTargetWriterClass(@Nonnull GenClass writerClass);

  @Nullable
  GenClass getTargetWriterClass();

  void setTargetMarshallerClass(@Nonnull GenClass marshallerClass);

  @Nullable
  GenClass getTargetMarshallerClass();
}
