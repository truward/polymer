package com.truward.polymer.code;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.truward.polymer.code.factory.AstFactory;
import com.truward.polymer.code.visitor.AstVoidVisitor;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Represents a factory class for AST nodes.
 *
 * @author Alexander Shabanov
 */
@Deprecated
public final class DefaultAstFactory implements AstFactory {
  private BiMap<FqName, Ast.Node> entities = HashBiMap.create(); // Node ::= Package||Class

  @Nonnull @Override public Ast.ClassDecl classDecl(@Nonnull Ast.Node parent, @Nullable String name) {
    final Ast.ClassDecl classDecl = new Ast.ClassDecl();

    parent.accept(new AstVoidVisitor<RuntimeException>() {
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
        // package-level nodes should be associated with the corresponding instances of compilation units
        classDecl.setCompilationUnit(new Ast.CompilationUnit());
      }
    });
    classDecl.setParent(parent);

    if (name != null) {
      classDecl.setName(name);
    }

    return classDecl;
  }

  @Nonnull @Override public Ast.ClassDecl classDecl(@Nonnull FqName className) {
    Ast.Node parent = Ast.Nil.INSTANCE;

    if (!className.isRoot()) {
      parent = getParentOrCreatePackage(className.getParent());
    }

    return classDecl(parent, className.getName());
  }

  @Nonnull @Override public Ast.TypeExpr classRef(@Nonnull Class<?> standardClass) {
    return new Ast.ClassRef(standardClass); // TODO: can be cached
  }

  @Override @Nonnull public Ast.VarDecl var(@Nonnull String name, @Nonnull Ast.TypeExpr typeExpr) {
    return new Ast.VarDecl(name).setTypeExpr(typeExpr);
  }

  @Override @Nonnull public Ast.VarDecl var(@Nonnull String name) {
    return var(name, nil());
  }

  @Override @Nonnull public Ast.Nil nil() {
    return Ast.Nil.INSTANCE;
  }

  @Override @Nonnull public Ast.Annotation annotation(@Nonnull Class<? extends Annotation> annotationClass) {
    return new Ast.Annotation().setTypeExpr(classRef(annotationClass));
  }

  @Nonnull @Override public Ast.TypeExpr voidType() {
    return classRef(void.class);
  }

  @Nonnull @Override public Ast.Package pkg(@Nonnull FqName fqName) {
    final Ast.Package result = AsPackageVisitor.asPackage(getParentOrCreatePackage(fqName));
    if (result == null) {
      throw new IllegalStateException("Non-package entity has been created for " + fqName);
    }
    return result;
  }

  @Nonnull @Override public Ast.Return returnStmt() {
    return new Ast.Return();
  }

  @Nonnull @Override public Ast.Return returnStmt(@Nonnull Ast.Expr expr) {
    return returnStmt().setExpr(expr);
  }

  @Nonnull @Override public Ast.Literal literal(@Nullable Object value) {
    return new Ast.Literal(value);
  }

  @Nonnull @Override public Ast.Ident ident(@Nonnull String name) {
    return new Ast.Ident(name);
  }

  @Nonnull @Override public Ast.Select select(@Nonnull Ast.Expr expr, @Nonnull String name) {
    return new Ast.Select(expr, name);
  }

  @Nonnull @Override public Ast.If ifStmt() {
    return new Ast.If();
  }

  @Nonnull @Override public Ast.If ifStmt(@Nonnull Ast.Expr condition, @Nonnull Ast.Stmt thenStmt) {
    return ifStmt().setCondition(condition).setThenPart(thenStmt);
  }

  @Nonnull @Override public Ast.If ifStmt(@Nonnull Ast.Expr condition, @Nonnull Ast.Stmt thenStmt, @Nonnull Ast.Stmt elseStmt) {
    return ifStmt(condition, thenStmt).setElsePart(elseStmt);
  }

  @Nonnull @Override public Ast.Conditional ifCond() {
    return new Ast.Conditional();
  }

  @Nonnull @Override public Ast.Conditional ifCond(@Nonnull Ast.Expr condition, @Nonnull Ast.Expr thenExpr) {
    return ifCond().setCondition(condition).setThenPart(thenExpr);
  }

  @Nonnull @Override public Ast.Conditional ifCond(@Nonnull Ast.Expr condition, @Nonnull Ast.Expr thenExpr, @Nonnull Ast.Expr elseExpr) {
    return ifCond(condition, thenExpr).setElsePart(elseExpr);
  }

  @Nonnull @Override public Ast.Call call(@Nonnull String methodName) {
    return new Ast.Call(methodName);
  }

  @Nonnull @Override public Ast.Call call(@Nonnull String methodName, @Nonnull Ast.Expr... arguments) {
    return call(methodName).addArgs(Arrays.asList(arguments));
  }

  @Nonnull @Override public Ast.Binary binary(@Nonnull Operator operator) {
    return new Ast.Binary(operator);
  }

  @Nonnull @Override public Ast.Binary binary(@Nonnull Operator operator, @Nonnull Ast.Expr leftSide, @Nonnull Ast.Expr rightSide) {
    return binary(operator).setLeftSide(leftSide).setRightSide(rightSide);
  }

  @Nonnull @Override public Ast.Unary unary(@Nonnull Operator operator) {
    return new Ast.Unary(operator);
  }

  @Nonnull @Override public Ast.Unary unary(@Nonnull Operator operator, @Nonnull Ast.Expr expr) {
    return unary(operator).setExpr(expr);
  }

  @Nonnull @Override public Ast.Assignment assignment() {
    return new Ast.Assignment();
  }

  @Nonnull @Override public Ast.Assignment assignment(@Nonnull Ast.Expr left, @Nonnull Ast.Expr right) {
    return assignment().setLeftSide(left).setRightSide(right);
  }

  @Nonnull @Override public Ast.ExprStmt exprStmt(@Nonnull Ast.Expr expr) {
    return new Ast.ExprStmt(expr);
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
      result = new Ast.Package(nil(), fqName.getName());
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

  private static final class AsPackageVisitor extends AstVoidVisitor<RuntimeException> {
    private Ast.Package result;

    @Override protected void visitNode(@Nonnull Ast.Node node) {
      // do nothing
    }

    @Override public void visitPackage(@Nonnull Ast.Package node) {
      result = node;
    }

    @Nullable static Ast.Package asPackage(@Nonnull Ast.Node node) {
      final AsPackageVisitor visitor = new AsPackageVisitor();
      node.accept(visitor);
      return visitor.result;
    }
  }
}
