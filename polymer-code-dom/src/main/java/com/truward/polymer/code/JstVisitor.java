package com.truward.polymer.code;

import javax.annotation.Nonnull;

/**
 * Visitor for the Java Syntax Tree Nodes
 *
 * @param <E> Thrown exception
 */
public abstract class JstVisitor<E extends Exception> {
  public void visitNode(@Nonnull Jst.Node n) throws E {
    throw new IllegalStateException(String.format(
        "Node %s is not handled by visitor %s", n.getClass(), getClass()));
  }

  public void visitUnit(@Nonnull Jst.Unit node) throws E { visitNode(node); }

  public void visitImport(@Nonnull Jst.Unit node) throws E { visitNode(node); }

  public void visitClass(@Nonnull Jst.ClassDeclaration node) throws E { visitNode(node); }

  public void visitMethod(@Nonnull Jst.MethodDeclaration node) throws E { visitNode(node); }

  public void visitVar(@Nonnull Jst.VarDeclaration node) throws E { visitNode(node); }

  public void visitEmptyStatement(@Nonnull Jst.EmptyStatement node) throws E { visitNode(node); }

  public void visitEmptyExpression(@Nonnull Jst.EmptyExpression node) throws E { visitNode(node); }

  public void visitBlock(@Nonnull Jst.Block node) throws E { visitNode(node); }

  public void visitDoWhileLoop(@Nonnull Jst.DoWhileLoop node) throws E { visitNode(node); }

  public void visitWhileLoop(@Nonnull Jst.WhileLoop node) throws E { visitNode(node); }

  public void visitForLoop(@Nonnull Jst.ForLoop node) throws E { visitNode(node); }

  public void visitForEachLoop(@Nonnull Jst.ForEachLoop node) throws E { visitNode(node); }

  public void visitLabeled(@Nonnull Jst.Labeled node) throws E { visitNode(node); }

  public void visitSwitch(@Nonnull Jst.Switch node) throws E { visitNode(node); }

  public void visitCase(@Nonnull Jst.Case node) throws E { visitNode(node); }

  public void visitSynchronized(@Nonnull Jst.Synchronized node) throws E { visitNode(node); }

  public void visitTry(@Nonnull Jst.Try node) throws E { visitNode(node); }

  public void visitCatch(@Nonnull Jst.Catch node) throws E { visitNode(node); }

  public void visitConditionalExpression(@Nonnull Jst.Conditional node) throws E { visitNode(node); }

  public void visitIf(@Nonnull Jst.If node) throws E { visitNode(node); }

  public void visitExpressionStatement(@Nonnull Jst.ExpressionStatement node) throws E { visitNode(node); }

  public void visitBreak(@Nonnull Jst.Break node) throws E { visitNode(node); }

  public void visitContinue(@Nonnull Jst.Continue node) throws E { visitNode(node); }

  public void visitReturn(@Nonnull Jst.Return node) throws E { visitNode(node); }

  public void visitThrow(@Nonnull Jst.Throw node) throws E { visitNode(node); }

  public void visitAssert(@Nonnull Jst.Assert node) throws E { visitNode(node); }

  public void visitCall(@Nonnull Jst.Call node) throws E { visitNode(node); }

  public void visitNewClass(@Nonnull Jst.NewClass node) throws E { visitNode(node); }

  public void visitNewArray(@Nonnull Jst.NewArray node) throws E { visitNode(node); }

  public void visitParens(@Nonnull Jst.Parens node) throws E { visitNode(node); }

  public void visitAssignment(@Nonnull Jst.Assignment node) throws E { visitNode(node); }

  public void visitCompoundAssignment(@Nonnull Jst.CompoundAssignment node) throws E { visitNode(node); }

  public void visitUnary(@Nonnull Jst.Unary node) throws E { visitNode(node); }

  public void visitBinary(@Nonnull Jst.Binary node) throws E { visitNode(node); }

  public void visitTypeCast(@Nonnull Jst.TypeCast node) throws E { visitNode(node); }

  public void visitInstanceOf(@Nonnull Jst.InstanceOf node) throws E { visitNode(node); }

  public void visitArrayAccess(@Nonnull Jst.ArrayAccess node) throws E { visitNode(node); }

  public void visitSelect(@Nonnull Jst.Selector node) throws E { visitNode(node); }

  public void visitIdentifier(@Nonnull Jst.Identifier node) throws E { visitNode(node); }

  public void visitLiteral(@Nonnull Jst.Literal node) throws E { visitNode(node); }

  public void visitAnnotation(@Nonnull Jst.Annotation node) throws E { visitNode(node); }

  //
  // types
  //

//    public void visitPrimitiveType(@Nonnull {Primitive?} node) throws E { visitNode(node); }
//
//    public void visitArrayType(@Nonnull {Array?} node) throws E { visitNode(node); }
//
//    public void visitParameterizedType(@Nonnull {Parameterized?} node) throws E { visitNode(node); }
//
//    public void visitTypeParameter(@Nonnull {TypeParameter?} node) throws E { visitNode(node); }

  public void visitWildcard(@Nonnull Jst.Wildcard node) throws E { visitNode(node); }
}
