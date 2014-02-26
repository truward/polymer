package com.truward.polymer.core.code.typed;

import com.truward.polymer.core.code.CodeObject;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface FqNamedObject extends CodeObject {

  void setFqName(@Nonnull FqName name);

  @Nonnull
  FqName getFqName();
}
