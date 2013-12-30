package com.truward.polymer.domain.analysis;

import com.truward.polymer.core.trait.TraitContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Represents information about domain field.
 */
public interface DomainField extends TraitContainer {
  @Nonnull
  String getFieldName();

  @Nonnull
  String getGetterName();

  @Nonnull
  Type getFieldType();
}
