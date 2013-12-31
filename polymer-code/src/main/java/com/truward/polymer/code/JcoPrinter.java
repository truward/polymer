package com.truward.polymer.code;

import com.truward.polymer.code.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Alexander Shabanov
 */
public final class JcoPrinter {
  private IndentationAwarePrinter printer;

  public JcoPrinter(@Nonnull PrintStream writer) {
    this.printer = new IndentationAwarePrinter(writer);
  }

  public void print(@Nonnull Jco.ClassModule module) {
    printComment(module.getModuleComment());

    // package name
    if (module.getPackageName() != null) {
      printer.printText("package").printChar(' ');
      printFqName(module.getPackageName());
      printer.printChar(';').printChar('\n');
    }

    printer.printChar('\n');

    // imports
    for (final Jco.Import importDecl : module.getImports()) {
      printer.printText("import").printChar('\n');
      if (importDecl.isStatic()) {
        printer.printText("static").printChar('\n');
      }
      printFqName(importDecl.getQualifier());
      printer.printChar(';').printChar('\n');
    }

    // classes
    for (final Jco.ClassDecl classDecl : module.getClassDecls()) {
      printClassDecl(classDecl);
    }
  }

  //
  // Private
  //

  private void printClassDecl(@Nonnull Jco.ClassDecl classDecl) {
    printComment(classDecl.getComment());
    for (final Jco.Annotation annotation : classDecl.getAnnotations()) {
      printAnnotation(annotation, true);
    }
    for (final Jco.Modifier modifier : classDecl.getModifiers()) {
      printModifier(modifier);
    }
    printer.printText("class").printChar(' ').printText(classDecl.getName()).printChar(' ').printChar('{');

    // TODO: impl
    printer.printText("/* TODO: class body */").printChar('\n');
    printer.printChar('}').printChar('\n');
  }

  private void printModifier(@Nonnull Jco.Modifier modifier) {
    // TODO: impl
    printer.printText("/* modifier */").printChar(' ');
  }

  private void printAnnotation(@Nonnull Jco.Annotation annotation, boolean addNewline) {
    // TODO: impl
    printer.printText("/* TODO: impl annotation */").printChar(addNewline ? '\n' : ' ');
  }

  private void printFqName(@Nonnull FqName fqName) {
    try {
      fqName.appendTo(printer.out);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void printComment(@Nullable Jco.Comment comment) {
    if (comment == null) {
      return;
    }

    // TODO: impl
    printer.printText("/* TODO: impl!!! */").printChar('\n');
  }

  private final class PrintVisitor extends JcoVisitor {

  }

  private static final class IndentationAwarePrinter {
    public static final String DEFAULT_INDENT_UNIT = "  ";

    private final PrintStream out;
    private int indentationLevel = 0;
    private boolean doIndent = true;
    private final String indentUnit;

    public IndentationAwarePrinter(@Nonnull PrintStream out) {
      this.out = out;
      indentUnit = DEFAULT_INDENT_UNIT; // TODO: parameterizable indentation level
    }

//    @Override
//    public void visitCharSequence(CharSequence obj) {
//      printText(obj);
//    }
//
//    @Override
//    public void visitChar(char obj) {
//      printChar(obj);
//    }
//
//    @Override
//    public void visitList(List<?> obj) {
//      for (final Object child : obj) {
//        CodeObjectVisitor.apply(this, child);
//      }
//    }
//
//    @Override
//    public void visitSingleLineComment(SingleLineComment node) {
//      for (final Object line : node.getLines()) {
//        printText("// ");
//        CodeObjectVisitor.apply(this, line);
//        printChar('\n');
//      }
//    }
//
//    @Override
//    public void visitCommentBlock(CommentBlock node) {
//      printText("/**").printChar('\n');
//      for (final Object line : node.getLines()) {
//        printText(" * ");
//        CodeObjectVisitor.apply(this, line);
//        printChar('\n');
//      }
//      printText(" */").printChar('\n');
//    }

    public IndentationAwarePrinter printText(CharSequence text) {
      indentIfNeeded();
      out.append(text);
      return this;
    }

    // TODO: simplify
    public IndentationAwarePrinter printChar(char ch) {
      boolean printFollowupNewlineAndReturn = false;
      if (ch > ' ') {
        if (ch == '}') {
          // special case for closing brace
          --indentationLevel;
          assert indentationLevel >= 0;
          printFollowupNewlineAndReturn = true;
        }

        // indent only non-whitespace started strings
        indentIfNeeded();
      } else {
        doIndent = false;
      }

      out.append(ch);

      if (printFollowupNewlineAndReturn) {
        out.append('\n');
        doIndent = true;
        return this;
      }

      switch (ch) {
        case '\n':
          doIndent = true;
          break;
        case '{':
          out.append('\n');
          doIndent = true;
          ++indentationLevel;
          break;
        case ';':
          // each semicolon will be followed by the newline
          out.append('\n');
          doIndent = true;
          break;
      }

      return this;
    }

    //
    // Private
    //

    private void indentIfNeeded() {
      if (doIndent) {
        assert indentationLevel >= 0;
        for (int i = 0; i < indentationLevel; ++i) {
          out.append(indentUnit);
        }
        doIndent = false;
      }
    }

  }

}