package com.truward.polymer.marshal.json.analysis;

import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.support.GenDomainClass;

import javax.annotation.Nonnull;

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
}
