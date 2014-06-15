package com.truward.polymer.code.visitor.parent;

import com.truward.polymer.code.Jst;

import javax.annotation.Nonnull;

/**
 * Interface to the parent-provider manager.
 *
 * @author Alexander Shabanov
 */
public interface ParentProvider {

  @Nonnull
  Jst.Node getParent();
}
