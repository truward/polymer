package com.truward.polymer.code;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public abstract class AstVoidVisitor {
  protected void visitNode(@Nonnull Ast.Node node) {
    throw new IllegalStateException(String.format("Node %s is not handled by visitor %s", node.getClass(), getClass()));
  }

  public void visitNil(@Nonnull Ast.Nil node) {
    visitNode(node);
  }

  public void visitPackage(@Nonnull Ast.Package node) {
    visitNode(node);
  }

  public void visitClassDecl(@Nonnull Ast.ClassDecl node) {
    visitNode(node);
  }

  public void visitMethodDecl(@Nonnull Ast.MethodDecl node) {
    visitNode(node);
  }

  public void visitReturn(@Nonnull Ast.Return node) {
    visitNode(node);
  }

  public void visitLiteral(@Nonnull Ast.Literal node) {
    visitNode(node);
  }

  public void visitIdent(@Nonnull Ast.Ident node) {
    visitNode(node);
  }

  public static void apply(@Nonnull Ast.Node node, @Nonnull AstVoidVisitor visitor) {
    if (node instanceof Ast.Package) {
      visitor.visitPackage((Ast.Package) node);
    } else if (node instanceof Ast.ClassDecl) {
      visitor.visitClassDecl((Ast.ClassDecl) node);
    } else if (node instanceof Ast.MethodDecl) {
      visitor.visitMethodDecl((Ast.MethodDecl) node);
    } else if (node instanceof Ast.Literal) {
      visitor.visitLiteral((Ast.Literal) node);
    } else if (node instanceof Ast.Return) {
      visitor.visitReturn((Ast.Return) node);
    } else if (node instanceof Ast.Ident) {
      visitor.visitIdent((Ast.Ident) node);
    } else if (node instanceof Ast.Nil) {
      visitor.visitNil((Ast.Nil) node);
    } else {
      visitor.visitNode(node);
    }
  }
}
