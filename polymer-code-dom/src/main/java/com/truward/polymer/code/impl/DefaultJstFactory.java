package com.truward.polymer.code.impl;

import com.truward.polymer.code.Jst;
import com.truward.polymer.code.JstFactory;
import com.truward.polymer.code.Operator;
import com.truward.polymer.code.TypeBoundKind;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public class DefaultJstFactory implements JstFactory {
  @Nonnull @Override public Jst.Unit jstUnit(@Nonnull FqName packageName) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Import jstImport(@Nonnull FqName importName, boolean isStatic) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.ClassDeclaration jstClass(@Nonnull String name) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.MethodDeclaration jstMethod(@Nonnull String name) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.VarDeclaration jstVar(@Nonnull String name, @Nonnull Jst.TypeExpression type) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.EmptyStatement jstEmptyStatement() {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.EmptyExpression jstEmptyExpression() {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Block jstBlock() {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.InitializerBlock jstInitializerBlock(boolean isStatic) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.DoWhileLoop jstDoWhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.WhileLoop jstWhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.ForLoop jstForLoop(@Nonnull List<Jst.Statement> initializers,
                                                   @Nonnull Jst.Expression condition,
                                                   @Nonnull Jst.Expression step,
                                                   @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.ForEachLoop jstForEachLoop(@Nonnull Jst.VarDeclaration var,
                                                           @Nonnull Jst.Expression expression,
                                                           @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Labeled jstLabeled(@Nonnull String label, @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Switch jstSwitch(@Nonnull Jst.Expression selector, @Nonnull List<Jst.Case> cases) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Synchronized jstSynchronized(@Nonnull Jst.Expression lock, @Nonnull Jst.Block body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Try jstTry(@Nonnull Jst.Block body,
                                           @Nonnull List<Jst.Catch> catchers,
                                           @Nullable Jst.Block finalizer) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Catch jstCatch(@Nonnull Jst.VarDeclaration parameter, @Nonnull Jst.Block body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.If jstIf(@Nonnull Jst.Expression condition,
                                         @Nonnull Jst.Statement then,
                                         @Nullable Jst.Statement els) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Conditional jstConditional(@Nonnull Jst.Expression condition,
                                                           @Nonnull Jst.Expression thenPart,
                                                           @Nonnull Jst.Expression elsePart) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Break jstBreak(@Nullable String label) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Continue jstContinue(@Nullable String label) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Return jstReturn(@Nullable Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Throw jstThrow(@Nonnull Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Assert jstAssert(@Nonnull Jst.Expression expression,
                                                 @Nullable Jst.Expression detail) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Call jstCall(@Nonnull String methodName,
                                             @Nullable Jst.Expression base,
                                             @Nonnull List<Jst.TypeParameter> typeParameters,
                                             @Nonnull List<Jst.Expression> arguments) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.NewClass jstNewClass(@Nullable Jst.Expression enclosingExpression,
                                                     @Nonnull Jst.TypeExpression type,
                                                     @Nonnull List<Jst.TypeParameter> typeParameters,
                                                     @Nonnull List<Jst.Expression> arguments,
                                                     @Nullable Jst.Block classBody) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.NewArray jstNewArray(@Nonnull Jst.TypeExpression type,
                                                     @Nonnull List<Jst.Expression> dimensions,
                                                     @Nonnull List<Jst.Expression> initializers) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Parens jstParens(@Nonnull Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Assignment jstAssignment(@Nonnull Jst.Expression left, @Nonnull Jst.Expression right) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.CompoundAssignment jstCompoundAssignment(@Nonnull Operator operator,
                                                                         @Nonnull Jst.Expression left,
                                                                         @Nonnull Jst.Expression right) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Unary jstUnary(@Nonnull Operator operator, @Nonnull Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Binary jstBinary(@Nonnull Operator operator,
                                                 @Nonnull Jst.Expression left,
                                                 @Nonnull Jst.Expression right) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.ClassType jstClassType(@Nonnull Class<?> clazz) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.SynteticType jstSynteticType(@Nonnull FqName name) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Array jstArray(@Nonnull Jst.TypeExpression elementType) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.ParameterizedType jstParameterizedType(@Nonnull Jst.TypeExpression type,
                                                                       @Nonnull List<Jst.Expression> arguments) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Wildcard jstWildcard(@Nullable Jst.TypeBoundExpression typeBoundExpression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.TypeBoundExpression jstTypeBound(@Nonnull TypeBoundKind kind,
                                                                 @Nonnull Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }

  //
  // Private
  //
}
