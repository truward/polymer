package com.truward.polymer.code.visitor;

import com.truward.polymer.code.Ast;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public abstract class AstVoidVisitor<T extends Exception> {
  protected void visitNode(@Nonnull Ast.Node node) throws T {
    throw new IllegalStateException(String.format("Node %s is not handled by visitor %s", node.getClass(), getClass()));
  }

  public void visitNil(@Nonnull Ast.Nil node) throws T {
    visitNode(node);
  }

  public void visitPackage(@Nonnull Ast.Package node) throws T {
    visitNode(node);
  }

  public void visitClassDecl(@Nonnull Ast.ClassDecl node) throws T {
    visitNode(node);
  }

  public void visitMethodDecl(@Nonnull Ast.MethodDecl node) throws T {
    visitNode(node);
  }

  public void visitVarDecl(@Nonnull Ast.VarDecl node) throws T {
    visitNode(node);
  }

  public void visitClassRef(@Nonnull Ast.ClassRef node) throws T {
    visitNode(node);
  }

  public void visitReturn(@Nonnull Ast.Return node) throws T {
    visitNode(node);
  }

  public void visitLiteral(@Nonnull Ast.Literal node) throws T {
    visitNode(node);
  }

  public void visitIdent(@Nonnull Ast.Ident node) throws T {
    visitNode(node);
  }

  public static <T extends Exception> void apply(@Nonnull Ast.Node node, @Nonnull AstVoidVisitor<T> visitor) throws T {
    if (node instanceof Ast.Package) {
      visitor.visitPackage((Ast.Package) node);
    } else if (node instanceof Ast.ClassDecl) {
      visitor.visitClassDecl((Ast.ClassDecl) node);
    } else if (node instanceof Ast.MethodDecl) {
      visitor.visitMethodDecl((Ast.MethodDecl) node);
    } else if (node instanceof Ast.VarDecl) {
      visitor.visitVarDecl((Ast.VarDecl) node);
    } else if (node instanceof Ast.ClassRef) {
      visitor.visitClassRef((Ast.ClassRef) node);
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
