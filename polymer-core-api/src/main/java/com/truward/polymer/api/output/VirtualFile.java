package com.truward.polymer.api.output;

import com.truward.polymer.freezable.Freezable;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface VirtualFile<T extends ContentGenerator> extends Freezable {

  @Nonnull
  T getContentGenerator();

  boolean isFrozen();

  boolean isVoid();
}
