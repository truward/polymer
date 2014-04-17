package com.truward.polymer.code.factory;

import com.truward.polymer.code.Ast;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Delegating factory - for usage code.
 *
 * @author Alexander Shabanov
 */
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

  @Nonnull @Override public Ast.Return returnStmt() {
    return getDelegate().returnStmt();
  }

  @Nonnull @Override public Ast.Return returnStmt(@Nonnull Ast.Expr expr) {
    return getDelegate().returnStmt(expr);
  }

  @Nonnull @Override public Ast.Literal literal(@Nullable Object value) {
    return getDelegate().literal(value);
  }

  @Nonnull @Override public Ast.Ident ident(@Nonnull String name) {
    return getDelegate().ident(name);
  }

  @Nonnull @Override public Ast.Select select(@Nonnull Ast.Expr expr, @Nonnull String name) {
    return getDelegate().select(expr, name);
  }
}
