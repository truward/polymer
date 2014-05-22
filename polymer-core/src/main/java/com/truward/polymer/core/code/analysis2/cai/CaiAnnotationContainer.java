package com.truward.polymer.core.code.analysis2.cai;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Alexander Shabanov
 */
public interface CaiAnnotationContainer extends CaiNode {

  @Nonnull
  Collection<CaiAnnotation> getAnnotations();
}
