package com.truward.polymer.code;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.truward.polymer.code.visitor.AstVoidVisitor;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a factory class for AST nodes.
 *
 * @author Alexander Shabanov
 */
public class AstFactory {
  private BiMap<FqName, Ast.Node> entities = HashBiMap.create(); // Node ::= Package||Class

  @Nonnull public Ast.ClassDecl classDecl(@Nonnull Ast.Node parent, @Nullable String name) {
    final Ast.ClassDecl node = new Ast.ClassDecl();

    AstVoidVisitor.apply(parent, new AstVoidVisitor() {
      @Override
      protected void visitNode(@Nonnull Ast.Node node) {
        throw new UnsupportedOperationException("Class may not be a parent of " + node);
      }

      @Override
      public void visitNil(@Nonnull Ast.Nil node) {
        // OK: root class
      }

      @Override
      public void visitClassDecl(@Nonnull Ast.ClassDecl node) {
        // OK: nested class
      }

      @Override
      public void visitMethodDecl(@Nonnull Ast.MethodDecl node) {
        // OK: class in a method
      }

      @Override
      public void visitPackage(@Nonnull Ast.Package node) {
        checkHavePackage(node);
      }
    });
    node.setParent(parent);

    if (name != null) {
      node.setName(name);
    }

    return node;
  }

  @Nonnull public Ast.ClassDecl classDecl(@Nonnull FqName className) {
    Ast.Node parent = Ast.Nil.INSTANCE;

    if (!className.isRoot()) {
      parent = getParentOrCreatePackage(className.getParent());
    }

    return classDecl(parent, className.getName());
  }

  @Nonnull public Ast.TypeExpr classRef(@Nonnull Class<?> classRef) {
    return new Ast.ClassRef(classRef);
  }

  @Nonnull public Ast.TypeExpr voidType() {
    return classRef(void.class);
  }

  @Nonnull public Ast.Package pkg(@Nonnull FqName fqName) {
    final Ast.Package result = AsPackageVisitor.asPackage(getParentOrCreatePackage(fqName));
    if (result == null) {
      throw new IllegalStateException("Non-package entity has been created for " + fqName);
    }
    return result;
  }

  //
  // Private
  //

  private void checkHavePackage(@Nonnull Ast.Package pkg) {
    if (!entities.inverse().containsKey(pkg)) {
      throw new IllegalStateException("Unregistered package reference: " + pkg.getFqName());
    }
  }

  @Nonnull private Ast.Node getParentOrCreatePackage(@Nonnull FqName fqName) {
    Ast.Node result = entities.get(fqName);
    if (result != null) {
      return result;
    }

    // root package requested - add to packages list and return the result
    if (fqName.isRoot()) {
      result = new Ast.Package(null, fqName.getName());
    } else {
      final Ast.Package parent = AsPackageVisitor.asPackage(getParentOrCreatePackage(fqName.getParent()));
      if (parent == null) {
        throw new IllegalStateException("New package requested to be created on top of non-top-level package " + fqName);
      }

      result = new Ast.Package(parent, fqName.getName());
    }

    // add package
    entities.put(fqName, result);

    return result;
  }

  private static final class AsPackageVisitor extends AstVoidVisitor {
    private Ast.Package result;
    @Override protected void visitNode(@Nonnull Ast.Node node) {
      // do nothing
    }

    @Override public void visitPackage(@Nonnull Ast.Package node) {
      result = node;
    }

    @Nullable static Ast.Package asPackage(@Nonnull Ast.Node node) {
      final AsPackageVisitor visitor = new AsPackageVisitor();
      AstVoidVisitor.apply(node, visitor);
      return visitor.result;
    }
  }
}
