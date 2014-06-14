package com.truward.polymer.code;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a factory for java syntax tree nodes.
 *
 * @author Alexander Shabanov
 */
public interface JstFactory {
  @Nonnull Jst.Unit jstUnit(@Nonnull FqName packageName);

  @Nonnull Jst.Import jstImport(@Nonnull FqName importName, boolean isStatic);

  @Nonnull Jst.ClassDeclaration jstClass(@Nonnull String name);

  @Nonnull Jst.MethodDeclaration jstMethod(@Nonnull String name);

  @Nonnull Jst.VarDeclaration jstVar(@Nonnull String name, @Nonnull Jst.TypeExpression type);

  @Nonnull Jst.EmptyStatement jstEmptyStatement();

  @Nonnull Jst.EmptyExpression jstEmptyExpression();

  @Nonnull Jst.Block jstBlock();

  @Nonnull Jst.InitializerBlock jstInitializerBlock(boolean isStatic);

  @Nonnull Jst.DoWhileLoop jstDoWhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body);

  @Nonnull Jst.WhileLoop jstWhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body);

  @Nonnull Jst.ForLoop jstForLoop(@Nonnull List<Jst.Statement> initializers,
                                  @Nonnull Jst.Expression condition,
                                  @Nonnull Jst.Expression step,
                                  @Nonnull Jst.Statement body);

  @Nonnull Jst.ForEachLoop jstForEachLoop(@Nonnull Jst.VarDeclaration var,
                                          @Nonnull Jst.Expression expression,
                                          @Nonnull Jst.Statement body);

  @Nonnull Jst.Labeled jstLabeled(@Nonnull String label, @Nonnull Jst.Statement body);

  @Nonnull Jst.Switch jstSwitch(@Nonnull Jst.Expression selector, @Nonnull List<Jst.Case> cases);

  @Nonnull Jst.Synchronized jstSynchronized(@Nonnull Jst.Expression lock, @Nonnull Jst.Block body);

  @Nonnull Jst.Try jstTry(@Nonnull Jst.Block body, @Nonnull List<Jst.Catch> catchers, @Nullable Jst.Block finalizer);

  @Nonnull Jst.Catch jstCatch(@Nonnull Jst.VarDeclaration parameter, @Nonnull Jst.Block body);

  @Nonnull Jst.If jstIf(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement then, @Nullable Jst.Statement els);

  @Nonnull Jst.Conditional jstConditional(@Nonnull Jst.Expression condition,
                                          @Nonnull Jst.Expression thenPart,
                                          @Nonnull Jst.Expression elsePart);

  @Nonnull Jst.Break jstBreak(@Nullable String label);

  @Nonnull Jst.Continue jstContinue(@Nullable String label);

  @Nonnull Jst.Return jstReturn(@Nullable Jst.Expression expression);

  @Nonnull Jst.Throw jstThrow(@Nonnull Jst.Expression expression);

  @Nonnull Jst.Assert jstAssert(@Nonnull Jst.Expression expression, @Nullable Jst.Expression detail);

  @Nonnull Jst.Call jstCall(@Nonnull String methodName,
                            @Nullable Jst.Expression base,
                            @Nonnull List<Jst.TypeParameter> typeParameters,
                            @Nonnull List<Jst.Expression> arguments);

  @Nonnull Jst.NewClass jstNewClass(@Nullable Jst.Expression enclosingExpression,
                                    @Nonnull Jst.TypeExpression type,
                                    @Nonnull List<Jst.TypeParameter> typeParameters,
                                    @Nonnull List<Jst.Expression> arguments,
                                    @Nullable Jst.Block classBody);

  @Nonnull Jst.NewArray jstNewArray(@Nonnull Jst.TypeExpression type,
                                    @Nonnull List<Jst.Expression> dimensions,
                                    @Nonnull List<Jst.Expression> initializers);

  @Nonnull Jst.Parens jstParens(@Nonnull Jst.Expression expression);

  @Nonnull Jst.Assignment jstAssignment(@Nonnull Jst.Expression left, @Nonnull Jst.Expression right);

  @Nonnull Jst.CompoundAssignment jstCompoundAssignment(@Nonnull Operator operator,
                                                        @Nonnull Jst.Expression left,
                                                        @Nonnull Jst.Expression right);

  @Nonnull Jst.Unary jstUnary(@Nonnull Operator operator, @Nonnull Jst.Expression expression);

  @Nonnull Jst.Binary jstBinary(@Nonnull Operator operator, @Nonnull Jst.Expression left, @Nonnull Jst.Expression right);
}
