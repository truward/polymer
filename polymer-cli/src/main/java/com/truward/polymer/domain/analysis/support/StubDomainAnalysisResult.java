package com.truward.polymer.domain.analysis.support;

import com.truward.polymer.core.trait.Trait;
import com.truward.polymer.core.trait.TraitKey;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

  @Nullable
  @Override
  public <T extends Trait> T findTrait(@Nonnull TraitKey<T> key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasTrait(@Nonnull TraitKey<?> key) {
    throw new UnsupportedOperationException();
  }

  @Nullable
  @Override
  public <T extends Trait> Trait putTrait(@Nonnull T trait) {
    throw new UnsupportedOperationException();
  }
}
