package com.truward.polymer.code.factory;

import com.truward.polymer.code.Ast;
import com.truward.polymer.code.Operator;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * @author Alexander Shabanov
 */
@Deprecated
public interface AstFactory {
  @Nonnull Ast.ClassDecl classDecl(@Nonnull Ast.Node parent, @Nullable String name);

  @Nonnull Ast.ClassDecl classDecl(@Nonnull FqName className);

  @Nonnull Ast.TypeExpr classRef(@Nonnull Class<?> classRef);

  @Nonnull Ast.VarDecl var(@Nonnull String name, @Nonnull Ast.TypeExpr typeExpr);

  @Nonnull Ast.VarDecl var(@Nonnull String name);

  @Nonnull Ast.Nil nil();

  @Nonnull Ast.Annotation annotation(@Nonnull Class<? extends Annotation> annotationClass);

  @Nonnull Ast.TypeExpr voidType();

  @Nonnull Ast.Package pkg(@Nonnull FqName fqName);

  @Nonnull Ast.Return returnStmt();

  @Nonnull Ast.Return returnStmt(@Nonnull Ast.Expr expr);

  @Nonnull Ast.Literal literal(@Nullable Object value);

  @Nonnull Ast.Ident ident(@Nonnull String name);

  @Nonnull Ast.Select select(@Nonnull Ast.Expr expr, @Nonnull String name);

  @Nonnull Ast.If ifStmt();

  @Nonnull Ast.If ifStmt(@Nonnull Ast.Expr condition, @Nonnull Ast.Stmt thenStmt);

  @Nonnull Ast.If ifStmt(@Nonnull Ast.Expr condition, @Nonnull Ast.Stmt thenStmt, @Nonnull Ast.Stmt elseStmt);

  @Nonnull Ast.Conditional ifCond();

  @Nonnull Ast.Conditional ifCond(@Nonnull Ast.Expr condition, @Nonnull Ast.Expr thenExpr);

  @Nonnull Ast.Conditional ifCond(@Nonnull Ast.Expr condition, @Nonnull Ast.Expr thenExpr, @Nonnull Ast.Expr elseExpr);

  @Nonnull Ast.Call call(@Nonnull String methodName);

  @Nonnull Ast.Call call(@Nonnull String methodName, @Nonnull Ast.Expr... arguments);

  @Nonnull Ast.Binary binary(@Nonnull Operator operator);

  @Nonnull Ast.Binary binary(@Nonnull Operator operator, @Nonnull Ast.Expr leftSide, @Nonnull Ast.Expr rightSide);

  @Nonnull Ast.Unary unary(@Nonnull Operator operator);

  @Nonnull Ast.Unary unary(@Nonnull Operator operator, @Nonnull Ast.Expr expr);

  @Nonnull Ast.Assignment assignment();

  @Nonnull Ast.Assignment assignment(@Nonnull Ast.Expr left, @Nonnull Ast.Expr right);

  @Nonnull Ast.ExprStmt exprStmt(@Nonnull Ast.Expr expr);
}
