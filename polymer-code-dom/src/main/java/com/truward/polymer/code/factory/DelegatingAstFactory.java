package com.truward.polymer.code.factory;

import com.truward.polymer.code.Ast;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

  @Override @Nonnull public Ast.TypeExpr voidType() {
    return getDelegate().voidType();
  }

  @Override @Nonnull public Ast.Package pkg(@Nonnull FqName fqName) {
    return getDelegate().pkg(fqName);
  }
}
