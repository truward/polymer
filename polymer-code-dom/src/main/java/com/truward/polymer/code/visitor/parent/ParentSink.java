package com.truward.polymer.code.visitor.parent;

import com.truward.polymer.code.Jst;

import javax.annotation.Nonnull;

/**
 * Write interface to provider manager.
 *
 * @author Alexander Shabanov
 */
public interface ParentSink {

  void push(@Nonnull Jst.Node node);

  void pop();
}
