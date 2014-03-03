package com.truward.polymer.marshal.gson.analysis;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a serialization strategy for certain field
 *
 * @author Alexander Shabanov
 */
public interface GsonField {

  void setJsonName(@Nonnull String name);

  @Nullable
  String getJsonName();
}
