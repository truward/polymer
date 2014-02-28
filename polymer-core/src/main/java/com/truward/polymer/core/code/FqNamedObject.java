package com.truward.polymer.core.code;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface FqNamedObject {

  void setFqName(@Nonnull FqName name);

  @Nonnull
  FqName getFqName();
}
