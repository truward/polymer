package com.truward.polymer.code.visitor.parent;

import com.truward.polymer.code.Jst;
import com.truward.polymer.code.visitor.JstVisitor;

import javax.annotation.Nonnull;

/**
 * A visitor, which maintains deque with parent nodes.
 *
 * @author Alexander Shabanov
 */
public final class JstParentAwareVisitor<T extends Exception> extends JstVisitor<T> {
  private final JstVisitor<T> delegate;
  private final ParentSink parents;

  public JstParentAwareVisitor(@Nonnull JstVisitor<T> delegate, @Nonnull ParentSink parents) {
    this.delegate = delegate;
    this.parents = parents;
  }

  @Override public void visitNode(@Nonnull Jst.Node node) throws T {
    parents.push(node);
    delegate.visitNode(node);
    parents.pop();
  }

  @Override public void visitWildcard(@Nonnull Jst.Wildcard node) throws T {
    parents.push(node);
    delegate.visitWildcard(node);
    parents.pop();
  }

  @Override public void visitUnit(@Nonnull Jst.Unit node) throws T {    
    parents.push(node);
    delegate.visitUnit(node);
    parents.pop();    
  }

  @Override public void visitImport(@Nonnull Jst.Import node) throws T {
    parents.push(node);
    delegate.visitImport(node);
    parents.pop();
  }

  @Override public void visitClass(@Nonnull Jst.ClassDeclaration node) throws T {
    parents.push(node);
    delegate.visitClass(node);
    parents.pop();
  }

  @Override public void visitMethod(@Nonnull Jst.MethodDeclaration node) throws T {
    parents.push(node);
    delegate.visitMethod(node);
    parents.pop();
  }

  @Override public void visitVar(@Nonnull Jst.VarDeclaration node) throws T {
    parents.push(node);
    delegate.visitVar(node);
    parents.pop();
  }

  @Override public void visitEmptyStatement(@Nonnull Jst.EmptyStatement node) throws T {
    parents.push(node);
    delegate.visitEmptyStatement(node);
    parents.pop();
  }

  @Override public void visitEmptyExpression(@Nonnull Jst.EmptyExpression node) throws T {
    parents.push(node);
    delegate.visitEmptyExpression(node);
    parents.pop();
  }

  @Override public void visitBlock(@Nonnull Jst.Block node) throws T {
    parents.push(node);
    delegate.visitBlock(node);
    parents.pop();
  }

  @Override public void visitInitializerBlock(@Nonnull Jst.InitializerBlock node) throws T {
    parents.push(node);
    delegate.visitInitializerBlock(node);
    parents.pop();
  }

  @Override public void visitDoWhileLoop(@Nonnull Jst.DoWhileLoop node) throws T {
    parents.push(node);
    delegate.visitDoWhileLoop(node);
    parents.pop();
  }

  @Override public void visitWhileLoop(@Nonnull Jst.WhileLoop node) throws T {
    parents.push(node);
    delegate.visitWhileLoop(node);
    parents.pop();
  }

  @Override public void visitForLoop(@Nonnull Jst.ForLoop node) throws T {
    parents.push(node);
    delegate.visitForLoop(node);
    parents.pop();
  }

  @Override public void visitForEachLoop(@Nonnull Jst.ForEachLoop node) throws T {
    parents.push(node);
    delegate.visitForEachLoop(node);
    parents.pop();
  }

  @Override public void visitLabeled(@Nonnull Jst.Labeled node) throws T {
    parents.push(node);
    delegate.visitLabeled(node);
    parents.pop();
  }

  @Override public void visitSwitch(@Nonnull Jst.Switch node) throws T {
    parents.push(node);
    delegate.visitSwitch(node);
    parents.pop();
  }

  @Override public void visitCase(@Nonnull Jst.Case node) throws T {
    parents.push(node);
    delegate.visitCase(node);
    parents.pop();
  }

  @Override public void visitSynchronized(@Nonnull Jst.Synchronized node) throws T {
    parents.push(node);
    delegate.visitSynchronized(node);
    parents.pop();
  }

  @Override public void visitTry(@Nonnull Jst.Try node) throws T {
    parents.push(node);
    delegate.visitTry(node);
    parents.pop();
  }

  @Override public void visitCatch(@Nonnull Jst.Catch node) throws T {
    parents.push(node);
    delegate.visitCatch(node);
    parents.pop();
  }

  @Override public void visitConditionalExpression(@Nonnull Jst.Conditional node) throws T {
    parents.push(node);
    delegate.visitConditionalExpression(node);
    parents.pop();
  }

  @Override public void visitIf(@Nonnull Jst.If node) throws T {
    parents.push(node);
    delegate.visitIf(node);
    parents.pop();
  }

  @Override public void visitExpressionStatement(@Nonnull Jst.ExpressionStatement node) throws T {
    parents.push(node);
    delegate.visitExpressionStatement(node);
    parents.pop();
  }

  @Override public void visitBreak(@Nonnull Jst.Break node) throws T {
    parents.push(node);
    delegate.visitBreak(node);
    parents.pop();
  }

  @Override public void visitContinue(@Nonnull Jst.Continue node) throws T {
    parents.push(node);
    delegate.visitContinue(node);
    parents.pop();
  }

  @Override public void visitReturn(@Nonnull Jst.Return node) throws T {
    parents.push(node);
    delegate.visitReturn(node);
    parents.pop();
  }

  @Override public void visitThrow(@Nonnull Jst.Throw node) throws T {
    parents.push(node);
    delegate.visitThrow(node);
    parents.pop();
  }

  @Override public void visitAssert(@Nonnull Jst.Assert node) throws T {
    parents.push(node);
    delegate.visitAssert(node);
    parents.pop();
  }

  @Override public void visitCall(@Nonnull Jst.Call node) throws T {
    parents.push(node);
    delegate.visitCall(node);
    parents.pop();
  }

  @Override public void visitNewClass(@Nonnull Jst.NewClass node) throws T {
    parents.push(node);
    delegate.visitNewClass(node);
    parents.pop();
  }

  @Override public void visitNewArray(@Nonnull Jst.NewArray node) throws T {
    parents.push(node);
    delegate.visitNewArray(node);
    parents.pop();
  }

  @Override public void visitParens(@Nonnull Jst.Parens node) throws T {
    parents.push(node);
    delegate.visitParens(node);
    parents.pop();
  }

  @Override public void visitAssignment(@Nonnull Jst.Assignment node) throws T {
    parents.push(node);
    delegate.visitAssignment(node);
    parents.pop();
  }

  @Override public void visitCompoundAssignment(@Nonnull Jst.CompoundAssignment node) throws T {
    parents.push(node);
    delegate.visitCompoundAssignment(node);
    parents.pop();
  }

  @Override public void visitUnary(@Nonnull Jst.Unary node) throws T {
    parents.push(node);
    delegate.visitUnary(node);
    parents.pop();
  }

  @Override public void visitBinary(@Nonnull Jst.Binary node) throws T {
    parents.push(node);
    delegate.visitBinary(node);
    parents.pop();
  }

  @Override public void visitTypeCast(@Nonnull Jst.TypeCast node) throws T {
    parents.push(node);
    delegate.visitTypeCast(node);
    parents.pop();
  }

  @Override public void visitInstanceOf(@Nonnull Jst.InstanceOf node) throws T {
    parents.push(node);
    delegate.visitInstanceOf(node);
    parents.pop();
  }

  @Override public void visitArrayAccess(@Nonnull Jst.ArrayAccess node) throws T {
    parents.push(node);
    delegate.visitArrayAccess(node);
    parents.pop();
  }

  @Override public void visitSelector(@Nonnull Jst.Selector node) throws T {
    parents.push(node);
    delegate.visitSelector(node);
    parents.pop();
  }

  @Override public void visitIdentifier(@Nonnull Jst.Identifier node) throws T {
    parents.push(node);
    delegate.visitIdentifier(node);
    parents.pop();
  }

  @Override public void visitLiteral(@Nonnull Jst.Literal node) throws T {
    parents.push(node);
    delegate.visitLiteral(node);
    parents.pop();
  }

  @Override public void visitAnnotation(@Nonnull Jst.Annotation node) throws T {
    parents.push(node);
    delegate.visitAnnotation(node);
    parents.pop();
  }

  @Override public void visitType(@Nonnull Jst.TypeExpression node) throws T {
    parents.push(node);
    delegate.visitType(node);
    parents.pop();
  }

  @Override public void visitSimpleClass(@Nonnull Jst.SimpleClassType node) throws T {
    parents.push(node);
    delegate.visitSimpleClass(node);
    parents.pop();
  }

  @Override public void visitClassType(@Nonnull Jst.ClassType node) throws T {
    parents.push(node);
    delegate.visitClassType(node);
    parents.pop();
  }

  @Override public void visitSynteticType(@Nonnull Jst.SynteticType node) throws T {
    parents.push(node);
    delegate.visitSynteticType(node);
    parents.pop();
  }

  @Override public void visitArray(@Nonnull Jst.Array node) throws T {
    parents.push(node);
    delegate.visitArray(node);
    parents.pop();
  }

  @Override public void visitParameterizedType(@Nonnull Jst.ParameterizedType node) throws T {
    parents.push(node);
    delegate.visitParameterizedType(node);
    parents.pop();
  }
}
