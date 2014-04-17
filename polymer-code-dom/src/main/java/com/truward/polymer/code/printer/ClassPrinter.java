package com.truward.polymer.code.printer;

import com.truward.polymer.code.Ast;
import com.truward.polymer.code.util.EscapeUtil;
import com.truward.polymer.code.visitor.AstVoidVisitor;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.printer.CAlikePrinter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
  private static final FqName JAVA_LANG_PACKAGE = FqName.valueOf("java.lang");
  private final GenericPrintingVisitor genericPrintingVisitor = new GenericPrintingVisitor();

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
      printGeneric(stmt);
    }

    printer.print('}'); // end of class declaration
  }

  private void printGeneric(@Nonnull Ast.Node node) throws IOException {
    node.accept(genericPrintingVisitor);
  }

  private final class GenericPrintingVisitor extends AstVoidVisitor<IOException> {
    @Override public void visitClassRef(@Nonnull Ast.ClassRef node) throws IOException {
      printClassNameReference(node.getFqName());
    }

    @Override public void visitVarDecl(@Nonnull Ast.VarDecl node) throws IOException {
      final Ast.VarDecl.Kind varKind = node.getFieldKind();
      printNamedStmt(node, varKind == Ast.VarDecl.Kind.FIELD);
      node.getTypeExpr().accept(this);
      printer.print(' ').print(node.getName());

      if (varKind != Ast.VarDecl.Kind.PARAMETER) {
        printer.print(';');
      }
    }

    @Override public void visitMethodDecl(@Nonnull Ast.MethodDecl node) throws IOException {
      printer.print('\n');

      printNamedStmt(node, true);
      node.getReturnType().accept(this);
      printer.print(' ').print(node.getName()).print('(');
      for (int i = 0; i < node.getArguments().size(); ++i) { // arguments
        if (i > 0) {
          printer.print(',').print(' ');
        }
        visitVarDecl(node.getArguments().get(i));
      }
      printer.print(')'); // end of arguments

      // throws
      if (!node.getThrown().isEmpty()) {
        printer.print("throws").print(' ');
        for (int i = 0; i < node.getThrown().size(); ++i) {
          if (i > 0) {
            printer.print(',').print(' ');
          }
          node.getThrown().get(i).accept(this);
        }
      }

      // method body
      if (node.getBody().isNil()) {
        printer.print(';');
      } else {
        printer.print(' ');
        node.getBody().accept(this);
      }
    }

    @Override public void visitBlock(@Nonnull Ast.Block node) throws IOException {
      printer.print('{');
      for (final Ast.Stmt stmt : node.getStatements()) {
        stmt.accept(this);
      }
      printer.print('}');
    }

    @Override public void visitReturn(@Nonnull Ast.Return node) throws IOException {
      printer.print("return");
      if (!node.getExpr().isNil()) {
        printer.print(' ');
        node.getExpr().accept(this);
      }
      printer.print(';');
    }

    @Override public void visitSelect(@Nonnull Ast.Select node) throws IOException {
      node.getExpr().accept(this);
      printer.print('.').print(node.getName());
    }

    @Override public void visitIdent(@Nonnull Ast.Ident node) throws IOException {
      printer.print(node.getName());
    }

    @Override public void visitLiteral(@Nonnull Ast.Literal node) throws IOException {
      printLiteral(node.getValue());
    }

    @Override public void visitAnnotation(@Nonnull Ast.Annotation node) throws IOException {
      printer.print('@');
      node.getTypeExpr().accept(this);
      if (!node.getArguments().isEmpty()) {
        printer.print('(');
        for (int i = 0; i < node.getArguments().size(); ++i) {
          if (i > 0) {
            printer.print(',').print(' ');
          }
          node.getArguments().get(i).accept(this);
        }
        printer.print(')');
      }
    }
  }

  private void printLiteral(@Nullable Object value) throws IOException {
    if (value == null) {
      printer.print("null");
    } else if (value instanceof String) {
      printer.print('\"');
      printer.print(EscapeUtil.escape((String) value));
      printer.print('\"');
    } else if (value instanceof Character) {
      printer.print('\'');
      printer.print(EscapeUtil.escape((Character) value));
      printer.print('\'');
    } else if (value instanceof Boolean) {
      printer.print(value.toString());
    } else if (value instanceof Number) {
      printer.print(value.toString());
      // suffix
      if (value instanceof Double) {
        printer.print('D');
      } else if (value instanceof Float) {
        printer.print('F');
      } else if (value instanceof Long) {
        printer.print('L');
      }
    } else {
      throw new IllegalArgumentException("Unknown literal: " + value);
    }
  }

  private void printClassNameReference(@Nonnull FqName className) throws IOException {
    if (!className.isRoot() && className.getParent().equals(JAVA_LANG_PACKAGE)) {
      // standard JDK class from java.lang
      printer.print(className.getName());
      return;
    }

    printer.print(className);
  }

  private void printNamedStmt(@Nonnull Ast.NamedStmt<?> namedStmt,
                              boolean putNewlineAfterAnnotations) throws IOException {
    for (final Ast.Annotation annotation : namedStmt.getAnnotations()) {
      genericPrintingVisitor.visitAnnotation(annotation);

      printer.print(putNewlineAfterAnnotations ? '\n' : ' ');
    }

    for (final Modifier modifier : namedStmt.getModifiers()) {
      // TODO: modifier names in switch block - minimize mem allocations
      printer.print(modifier.name().toLowerCase()).print(' ');
    }
  }
}
