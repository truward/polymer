package com.truward.polymer.api.cai;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface CaiAnnotated extends CaiNode {
  @Nonnull
  String getName();

  @Nonnull
  CaiAnnotationContainer getAnnotations();
}
