package com.truward.polymer.code.factory;

import com.truward.polymer.code.Ast;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexander Shabanov
 */
public interface AstFactory {
  @Nonnull Ast.ClassDecl classDecl(@Nonnull Ast.Node parent, @Nullable String name);

  @Nonnull Ast.ClassDecl classDecl(@Nonnull FqName className);

  @Nonnull Ast.TypeExpr classRef(@Nonnull Class<?> classRef);

  @Nonnull Ast.TypeExpr voidType();

  @Nonnull Ast.Package pkg(@Nonnull FqName fqName);
}
