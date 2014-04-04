package com.truward.polymer.marshal.json.analysis;

import javax.annotation.Nonnull;

/**
 * Represents a serialization strategy for certain field
 *
 * @author Alexander Shabanov
 */
public interface JsonField {

  void setJsonName(@Nonnull String name);

  @Nonnull
  String getJsonName();

  boolean hasJsonName();
}
