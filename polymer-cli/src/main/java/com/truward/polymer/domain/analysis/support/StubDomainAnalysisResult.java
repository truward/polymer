package com.truward.polymer.domain.analysis.support;

import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Alexander Shabanov
 */
public final class StubDomainAnalysisResult implements DomainAnalysisResult {
  private static final DomainAnalysisResult INSTANCE = new StubDomainAnalysisResult();

  private StubDomainAnalysisResult() {}

  public static DomainAnalysisResult getInstance() {
    return INSTANCE;
  }

  @Nonnull
  @Override
  public Class<?> getOriginClass() {
    throw new UnsupportedOperationException();
  }

  @Nonnull
  @Override
  public Collection<? extends DomainField> getDeclaredFields() {
    throw new UnsupportedOperationException();
  }

  @Nonnull
  @Override
  public Collection<DomainAnalysisResult> getParents() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isStub() {
    return true;
  }
}
