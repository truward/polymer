package com.truward.polymer.api.cai;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface CaiNode {

  void apply(@Nonnull CaiVisitor visitor);
}
