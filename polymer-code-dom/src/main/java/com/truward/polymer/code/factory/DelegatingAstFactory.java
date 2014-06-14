package com.truward.polymer.code.factory;

import com.truward.polymer.code.Ast;
import com.truward.polymer.code.Operator;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Delegating factory - for usage code.
 *
 * @author Alexander Shabanov
 */
@Deprecated
public abstract class DelegatingAstFactory implements AstFactory {
  @Nonnull protected abstract AstFactory getDelegate();

  @Override @Nonnull public Ast.ClassDecl classDecl(@Nonnull Ast.Node parent, @Nullable String name) {
    return getDelegate().classDecl(parent, name);
  }

  @Override @Nonnull public Ast.ClassDecl classDecl(@Nonnull FqName className) {
    return getDelegate().classDecl(className);
  }

  @Override @Nonnull public Ast.TypeExpr classRef(@Nonnull Class<?> classRef) {
    return getDelegate().classRef(classRef);
  }

  @Override @Nonnull public Ast.VarDecl var(@Nonnull String name, @Nonnull Ast.TypeExpr typeExpr) {
    return getDelegate().var(name, typeExpr);
  }

  @Override @Nonnull public Ast.VarDecl var(@Nonnull String name) {
    return getDelegate().var(name);
  }

  @Override @Nonnull public Ast.Nil nil() {
    return getDelegate().nil();
  }

  @Override @Nonnull public Ast.Annotation annotation(@Nonnull Class<? extends Annotation> annotationClass) {
    return getDelegate().annotation(annotationClass);
  }

  @Override @Nonnull public Ast.TypeExpr voidType() {
    return getDelegate().voidType();
  }

  @Override @Nonnull public Ast.Package pkg(@Nonnull FqName fqName) {
    return getDelegate().pkg(fqName);
  }

  @Override @Nonnull public Ast.Return returnStmt() {
    return getDelegate().returnStmt();
  }

  @Override @Nonnull public Ast.Return returnStmt(@Nonnull Ast.Expr expr) {
    return getDelegate().returnStmt(expr);
  }

  @Override @Nonnull public Ast.Literal literal(@Nullable Object value) {
    return getDelegate().literal(value);
  }

  @Override @Nonnull public Ast.Ident ident(@Nonnull String name) {
    return getDelegate().ident(name);
  }

  @Override @Nonnull public Ast.Select select(@Nonnull Ast.Expr expr, @Nonnull String name) {
    return getDelegate().select(expr, name);
  }

  @Override @Nonnull public Ast.If ifStmt() {
    return getDelegate().ifStmt();
  }

  @Override @Nonnull public Ast.If ifStmt(@Nonnull Ast.Expr condition, @Nonnull Ast.Stmt thenStmt) {
    return getDelegate().ifStmt(condition, thenStmt);
  }

  @Override @Nonnull public Ast.If ifStmt(@Nonnull Ast.Expr condition, @Nonnull Ast.Stmt thenStmt, @Nonnull Ast.Stmt elseStmt) {
    return getDelegate().ifStmt(condition, thenStmt, elseStmt);
  }

  @Override @Nonnull public Ast.Conditional ifCond() {
    return getDelegate().ifCond();
  }

  @Override @Nonnull public Ast.Conditional ifCond(@Nonnull Ast.Expr condition, @Nonnull Ast.Expr thenExpr) {
    return getDelegate().ifCond(condition, thenExpr);
  }

  @Override @Nonnull public Ast.Conditional ifCond(@Nonnull Ast.Expr condition, @Nonnull Ast.Expr thenExpr, @Nonnull Ast.Expr elseExpr) {
    return getDelegate().ifCond(condition, thenExpr, elseExpr);
  }

  @Override @Nonnull public Ast.Call call(@Nonnull String methodName) {
    return getDelegate().call(methodName);
  }

  @Override @Nonnull public Ast.Call call(@Nonnull String methodName, @Nonnull Ast.Expr... arguments) {
    return getDelegate().call(methodName, arguments);
  }

  @Override @Nonnull public Ast.Binary binary(@Nonnull Operator operator) {
    return getDelegate().binary(operator);
  }

  @Override @Nonnull public Ast.Binary binary(@Nonnull Operator operator, @Nonnull Ast.Expr leftSide, @Nonnull Ast.Expr rightSide) {
    return getDelegate().binary(operator, leftSide, rightSide);
  }

  @Override @Nonnull public Ast.Unary unary(@Nonnull Operator operator) {
    return getDelegate().unary(operator);
  }

  @Override @Nonnull public Ast.Unary unary(@Nonnull Operator operator, @Nonnull Ast.Expr expr) {
    return getDelegate().unary(operator, expr);
  }

  @Override @Nonnull public Ast.Assignment assignment() {
    return getDelegate().assignment();
  }

  @Override @Nonnull public Ast.Assignment assignment(@Nonnull Ast.Expr left, @Nonnull Ast.Expr right) {
    return getDelegate().assignment(left, right);
  }

  @Override @Nonnull public Ast.ExprStmt exprStmt(@Nonnull Ast.Expr expr) {
    return getDelegate().exprStmt(expr);
  }
}
