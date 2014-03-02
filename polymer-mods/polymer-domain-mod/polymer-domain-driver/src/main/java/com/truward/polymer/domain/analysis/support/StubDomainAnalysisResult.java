package com.truward.polymer.domain.analysis.support;

import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import java.util.List;

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
  public List<DomainField> getFields() {
    throw new UnsupportedOperationException();
  }

  @Nonnull
  @Override
  public List<DomainField> getDeclaredFields() {
    throw new UnsupportedOperationException();
  }

  @Nonnull
  @Override
  public List<DomainAnalysisResult> getParents() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isStub() {
    return true;
  }
}
