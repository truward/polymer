package com.truward.polymer.code;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public abstract class JcoVisitor {
  public void visitNode(@Nonnull Jco.Node node) {
    throw new IllegalStateException("Unhandled node " + node);
  }

  public void visitExpr(@Nonnull Jco.Expr node) {
    visitNode(node);
  }

  public void visitCharExpr(@Nonnull Jco.CharExpr node) {
    visitExpr(node);
  }

  public void visitText(@Nonnull Jco.Text node) {
    visitExpr(node);
  }

  public static void apply(@Nonnull JcoVisitor visitor, @Nonnull Jco.Node node) {
    if (node instanceof Jco.CharExpr) {
      visitor.visitCharExpr((Jco.CharExpr) node);
    } else if (node instanceof Jco.Text) {
      visitor.visitText((Jco.Text) node);
    } else {
      visitor.visitNode(node);
    }
  }
}
