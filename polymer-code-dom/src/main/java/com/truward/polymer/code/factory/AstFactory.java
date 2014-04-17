package com.truward.polymer.code.factory;

import com.truward.polymer.code.Ast;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * @author Alexander Shabanov
 */
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
}
