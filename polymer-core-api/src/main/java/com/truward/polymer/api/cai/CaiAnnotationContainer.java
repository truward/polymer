package com.truward.polymer.api.cai;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Alexander Shabanov
 */
public interface CaiAnnotationContainer extends CaiNode {

  @Nonnull
  Collection<CaiAnnotation> getAnnotations();
}
