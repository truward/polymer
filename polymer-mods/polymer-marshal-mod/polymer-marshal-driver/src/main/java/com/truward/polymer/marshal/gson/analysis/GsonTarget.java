package com.truward.polymer.marshal.gson.analysis;

import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.support.GenDomainClass;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface GsonTarget extends Freezable {
  @Nonnull
  DomainAnalysisResult getDomainAnalysisResult();

  @Nonnull
  GenDomainClass getDomainClass();

  @Nonnull
  GenTypeAdapterClass getTypeAdapter();

  boolean isReaderSupportRequested();

  boolean isWriterSupportRequested();
}
