package com.truward.polymer.core.code.typed;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface GenArray extends GenType {
  @Nonnull
  GenType getElementType();
}
