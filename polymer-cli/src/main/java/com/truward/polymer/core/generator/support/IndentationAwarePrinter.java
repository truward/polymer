package com.truward.polymer.core.generator.support;

import com.truward.polymer.core.generator.model.*;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.List;

/**
 * Print visitor for languages with C-alike rules of indentation, that implies logical blocks being enclosed in
 * curly braces and each statement being terminated with semicolon.
 * Will work for java, C, C++, C#, CSS and to some extent javascript (assuming statements necessarily terminated with
 * semicolons, though, strictly speaking, it is not required by the javascript standard).
 *
 * @author Alexander Shabanov
 */
public class IndentationAwarePrinter extends CodeObjectVisitor implements CodeObjectPrinter {
  public static final String DEFAULT_INDENT_UNIT = "  ";

  private final PrintStream out;
  private int indentationLevel = 0;
  private boolean doIndent = true;
  private final String indentUnit;

  public IndentationAwarePrinter(@Nonnull PrintStream out) {
    this.out = out;
    indentUnit = DEFAULT_INDENT_UNIT; // TODO: parameterizable indentation level
  }

  @Override
  public void print(@Nonnull Object obj) {
    CodeObjectVisitor.apply(this, obj);
  }

  @Override
  public void visitCharSequence(CharSequence obj) {
    printText(obj);
  }

  @Override
  public void visitChar(char obj) {
    printChar(obj);
  }

  @Override
  public void visitList(List<?> obj) {
    for (final Object child : obj) {
      CodeObjectVisitor.apply(this, child);
    }
  }

  @Override
  public void visitSingleLineComment(SingleLineComment node) {
    for (final Object line : node.getLines()) {
      printText("// ");
      CodeObjectVisitor.apply(this, line);
      printChar('\n');
    }
  }

  @Override
  public void visitCommentBlock(CommentBlock node) {
    printText("/**").printChar('\n');
    for (final Object line : node.getLines()) {
      printText(" * ");
      CodeObjectVisitor.apply(this, line);
      printChar('\n');
    }
    printText(" */").printChar('\n');
  }

  @Override
  public void visitPrintable(Printable obj) {
    obj.print(this);
  }

  //
  // Private
  //

  private IndentationAwarePrinter printText(CharSequence text) {
    indentIfNeeded();
    out.append(text);
    return this;
  }

  // TODO: simplify
  private IndentationAwarePrinter printChar(char ch) {
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
