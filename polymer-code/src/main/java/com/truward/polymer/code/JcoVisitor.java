package com.truward.polymer.code;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public abstract class JcoVisitor {
  public void visitNode(@Nonnull Jco.Node node) {
    throw new IllegalStateException("Unhandled node " + node);
  }

  //public void visitClassDecl(@N)


}
