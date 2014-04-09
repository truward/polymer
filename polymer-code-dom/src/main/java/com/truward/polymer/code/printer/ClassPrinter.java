package com.truward.polymer.code.printer;

import com.truward.polymer.code.Ast;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.printer.CAlikePrinter;

import javax.annotation.Nonnull;
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

    printImports(printer, classDecl);
    printClassDecl(printer, classDecl);
  }


  //
  // Private
  //

  private void printImports(@Nonnull CAlikePrinter printer, @Nonnull Ast.ClassDecl classDecl) throws IOException {
    for (final Ast.Import astImport : classDecl.getCompilationUnit().getImports()) {
      printer.print("import").print(' ');
      if (astImport.isStatic()) {
        printer.print("static").print(' ');
      }
      printer.print(astImport.getImportName()).print(';');
    }
    printer.print('\n');
  }

  private void printClassDecl(@Nonnull CAlikePrinter printer, @Nonnull Ast.ClassDecl classDecl) throws IOException {
    printer.print("class").print(' ').print(classDecl.getName()).print(' ').print('{');

    printer.print('}'); // end of class declaration
  }
}
