package com.truward.polymer.core.code.typed;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface GenParameterizedType extends GenType {
  @Nonnull
  GenType getRawType();

  @Nonnull
  List<GenType> getTypeParameters();
}
