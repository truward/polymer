package com.truward.polymer.core.code.analysis2.cai;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface CaiNode {

  void apply(@Nonnull CaiVisitor visitor);
}
