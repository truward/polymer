package com.truward.polymer.domain.analysis;

import com.truward.polymer.core.trait.TraitContainer;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Contains the result of analyzing certain class.
 *
 * @author Alexander Shabanov
 */
public interface DomainAnalysisResult extends TraitContainer {
  @Nonnull
  Class<?> getOriginClass();

  @Nonnull
  List<DomainField> getDeclaredFields();

  /**
   * Returns all the fields including the ones defined in parent fields
   *
   * @return List of fields
   */
  @Nonnull
  List<DomainField> getFields();

  @Nonnull
  List<DomainAnalysisResult> getParents();

  boolean isStub();
}
