package com.truward.polymer.code.visitor;

import com.truward.polymer.code.Ast;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
@Deprecated
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

  public void visitSelect(@Nonnull Ast.Select node) throws T {
    visitNode(node);
  }

  public void visitImport(@Nonnull Ast.Import node) throws T {
    visitNode(node);
  }

  public void visitBlock(@Nonnull Ast.Block node) throws T {
    visitNode(node);
  }

  public void visitArray(@Nonnull Ast.Array node) throws T {
    visitNode(node);
  }

  public void visitParameterizedClass(@Nonnull Ast.ParameterizedClass node) throws T {
    visitNode(node);
  }

  public void visitAnnotation(@Nonnull Ast.Annotation node) throws T {
    visitNode(node);
  }

  public void visitTypeParameter(@Nonnull Ast.TypeParameter node) throws T {
    visitNode(node);
  }

  public void visitWildcard(@Nonnull Ast.Wildcard node) throws T {
    visitNode(node);
  }

  public void visitTypeBoundExpr(@Nonnull Ast.TypeBoundExpr node) throws T {
    visitNode(node);
  }

  public void visitCompilationUnit(@Nonnull Ast.CompilationUnit node) throws T {
    visitNode(node);
  }

  public void visitIf(@Nonnull Ast.If node) throws T {
    visitNode(node);
  }

  public void visitConditional(@Nonnull Ast.Conditional node) throws T {
    visitNode(node);
  }

  public void visitCall(@Nonnull Ast.Call node) throws T {
    visitNode(node);
  }

  public void visitBinary(@Nonnull Ast.Binary node) throws T {
    visitNode(node);
  }

  public void visitUnary(@Nonnull Ast.Unary node) throws T {
    visitNode(node);
  }


  public void visitAssignment(@Nonnull Ast.Assignment node) throws T {
    visitNode(node);
  }


  public void visitExprStmt(@Nonnull Ast.ExprStmt node) throws T {
    visitNode(node);
  }
}
