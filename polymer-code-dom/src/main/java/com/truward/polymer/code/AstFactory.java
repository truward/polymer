package com.truward.polymer.code;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a factory class for AST nodes.
 *
 * @author Alexander Shabanov
 */
public class AstFactory {
  private BiMap<FqName, Ast.Package> packages = HashBiMap.create();

  @Nonnull public Ast.ClassDecl classDecl(@Nonnull Ast.Node parent) {
    final Ast.ClassDecl node = new Ast.ClassDecl();

    AstVoidVisitor.apply(parent, new AstVoidVisitor() {
      @Override protected void visitNode(@Nonnull Ast.Node node) {
        throw new UnsupportedOperationException("Class may not be a parent of " + node);
      }

      @Override public void visitClassDecl(@Nonnull Ast.ClassDecl node) {
        // OK
      }

      @Override public void visitMethodDecl(@Nonnull Ast.MethodDecl node) {
        // OK
      }

      @Override public void visitPackage(@Nonnull Ast.Package node) {
        checkHavePackage(node);
      }
    });
    node.setParent(parent);

    return node;
  }

  @Nonnull public Ast.ClassDecl classDecl(@Nonnull Ast.Node parent, @Nonnull String name) {
    final Ast.ClassDecl node = classDecl(parent);
    node.setName(name);
    return node;
  }

  @Nonnull public Ast.ClassDecl classDecl(@Nonnull FqName className) {
    Ast.Node parent = Ast.Nil.INSTANCE;

    if (!className.isRoot()) {
      parent = packages.get(className.getParent());
      if (parent == null) {
        throw new IllegalStateException("Host package " + className.getParent() + " is not registered");
      }
    }

    return classDecl(parent, className.getName());
  }

  @Nonnull public Ast.ClassRef ref(@Nonnull Class<?> classRef) {
    return new Ast.ClassRef(classRef);
  }

  //
  // Private
  //

  private void checkHavePackage(@Nonnull Ast.Package pkg) {
    if (!packages.inverse().containsKey(pkg)) {
      throw new IllegalStateException("Unregistered package reference: " + pkg.getFqName());
    }
  }

  private Ast.Package getOrCreatePackage(@Nonnull FqName fqName) {
    Ast.Package result = packages.get(fqName);
    if (result != null) {
      return result;
    }

    // root package requested - add to packages list and return the result
    if (fqName.isRoot()) {
      result = new Ast.Package(null, fqName.getName());
    } else {
      result = new Ast.Package(getOrCreatePackage(fqName.getParent()), fqName.getName());
    }

    // add package
    packages.put(fqName, result);

    return result;
  }
}
