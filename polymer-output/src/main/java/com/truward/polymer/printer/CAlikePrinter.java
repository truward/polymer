package com.truward.polymer.printer;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * Indentation-aware printer for C-alike language family, like C, C++, javascript and java.
 * Exposes fluent API in a form of overloaded print methods.
 *
 * @author Alexander Shabanov
 */
public class CAlikePrinter {
  public static final String DEFAULT_INDENT_UNIT = "  ";

  private final Writer writer;
  private int indentationLevel = 0;
  private boolean doIndent = true;
  private final String indentUnit;

  public CAlikePrinter(@Nonnull Writer writer, @Nonnull String indentUnit) {
    this.writer = writer;
    this.indentUnit = indentUnit;
  }

  public CAlikePrinter(@Nonnull Writer writer) {
    this(writer, DEFAULT_INDENT_UNIT);
  }

  @Nonnull
  public CAlikePrinter print(@Nonnull CharSequence text) throws IOException {
    indentIfNeeded();
    writer.append(text);
    return this;
  }

  @Nonnull
  public CAlikePrinter print(@Nonnull FqName fqName) throws IOException {
    indentIfNeeded();
    fqName.appendTo(writer);
    return this;
  }

  @Nonnull
  public CAlikePrinter print(char ch) throws IOException {
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

    writer.append(ch);

    if (printFollowupNewlineAndReturn) {
      writer.append('\n');
      doIndent = true;
      return this;
    }

    switch (ch) {
      case '\n':
        doIndent = true;
        break;
      case '{':
        writer.append('\n');
        doIndent = true;
        ++indentationLevel;
        break;
      case ';':
        // each semicolon will be followed by the newline
        writer.append('\n');
        doIndent = true;
        break;
    }

    return this;
  }

  //
  // Private
  //

  private void indentIfNeeded() throws IOException {
    if (doIndent) {
      assert indentationLevel >= 0;
      for (int i = 0; i < indentationLevel; ++i) {
        writer.append(indentUnit);
      }
      doIndent = false;
    }
  }
}
