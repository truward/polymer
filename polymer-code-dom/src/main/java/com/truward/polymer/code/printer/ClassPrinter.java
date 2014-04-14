package com.truward.polymer.code.printer;

import com.truward.polymer.code.Ast;
import com.truward.polymer.code.visitor.AstVoidVisitor;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.printer.CAlikePrinter;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.io.IOException;

/**
 * Helper class for printing class declaration.
 *
 * @author Alexander Shabanov
 */
final class ClassPrinter {
  private final CAlikePrinter printer;
  private final Ast.ClassDecl classDecl;

  ClassPrinter(@Nonnull CAlikePrinter printer, @Nonnull Ast.ClassDecl classDecl) {
    this.printer = printer;
    this.classDecl = classDecl;
  }

  void print() throws IOException {
    final FqName className = classDecl.getFqName();

    // package name
    if (!className.isRoot()) {
      printer.print("package").print(' ').print(className.getParent()).print(';').print('\n');
    }

    printImports(classDecl);
    printClassDecl(classDecl);
  }


  //
  // Private
  //

  private void printImports(@Nonnull Ast.ClassDecl classDecl) throws IOException {
    for (final Ast.Import astImport : classDecl.getCompilationUnit().getImports()) {
      printer.print("import").print(' ');
      if (astImport.isStatic()) {
        printer.print("static").print(' ');
      }
      printer.print(astImport.getImportName()).print(';');
    }
    printer.print('\n');
  }

  private void printClassDecl(@Nonnull Ast.ClassDecl classDecl) throws IOException {
    printNamedStmt(classDecl, true);
    printer.print("class").print(' ').print(classDecl.getName()).print(' ').print('{');

    for (final Ast.Stmt stmt : classDecl.getBodyStmts()) {
      printStmt(stmt);
      printer.print(';');
    }

    printer.print('}'); // end of class declaration
  }

  private void printStmt(@Nonnull Ast.Stmt stmt) throws IOException {
    AstVoidVisitor.apply(stmt, new AstVoidVisitor<IOException>() {
      @Override
      public void visitVarDecl(@Nonnull Ast.VarDecl node) throws IOException {
        final Ast.VarDecl.Kind varKind = node.getFieldKind();
        printNamedStmt(node, varKind == Ast.VarDecl.Kind.FIELD);
        printTypeExpr(node.getTypeExpr());
        printer.print(' ').print(node.getName());
      }
    });
  }

  private void printTypeExpr(@Nonnull Ast.TypeExpr typeExpr) throws IOException {
    AstVoidVisitor.apply(typeExpr, new AstVoidVisitor<IOException>() {
      @Override
      public void visitClassRef(@Nonnull Ast.ClassRef node) throws IOException {
        printer.print(node.getFqName());
      }
    });
  }

  private void printNamedStmt(@Nonnull Ast.NamedStmt<?> namedStmt,
                              boolean putNewlineAfterAnnotations) throws IOException {
    for (final Ast.Annotation annotation : namedStmt.getAnnotations()) {
      if (!annotation.toString().equals("1")) {
        // TODO: support for annotations
        throw new UnsupportedOperationException("Support for annotations");
      }

      printer.print(putNewlineAfterAnnotations ? '\n' : ' ');
    }

    for (final Modifier modifier : namedStmt.getModifiers()) {
      // TODO: modifier names in switch block - minimize mem allocations
      printer.print(modifier.name().toLowerCase()).print(' ');
    }
  }
}
