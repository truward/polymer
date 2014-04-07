package com.truward.polymer.code.visitor;

import com.truward.polymer.code.Ast;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public abstract class AstVisitor<R> {
  protected R visitNode(@Nonnull Ast.Node node) {
    throw new IllegalStateException(String.format("Node %s is not handled by visitor %s", node.getClass(), getClass()));
  }

  public R visitPackage(@Nonnull Ast.Package node) {
    return visitNode(node);
  }

  public R visitClassDecl(@Nonnull Ast.ClassDecl node) {
    return visitNode(node);
  }

  public static <R> R apply(@Nonnull Ast.Node node, @Nonnull AstVisitor<R> visitor) {
    // TODO: implement
    throw new UnsupportedOperationException();
  }
}
