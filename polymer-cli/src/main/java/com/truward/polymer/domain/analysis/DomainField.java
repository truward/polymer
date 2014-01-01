package com.truward.polymer.domain.analysis;

import com.truward.polymer.code.freezable.Freezable;
import com.truward.polymer.core.trait.TraitContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Represents information about domain field.
 */
public interface DomainField extends TraitContainer, Freezable {
  @Nonnull
  String getFieldName();

  @Nonnull
  Type getFieldType();
}
