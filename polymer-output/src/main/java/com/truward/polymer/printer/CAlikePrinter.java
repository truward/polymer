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

  private static enum State {
    NONE,
    INDENT,
    NEWLINE_AND_INDENT
  }

  private final Writer writer;
  private int indentationLevel = 0;
  private State state = State.NONE;
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
    if (ch > ' ') {
      if (ch == '}') {
        // special case for closing brace
        --indentationLevel;
        writer.append('}');
        assert indentationLevel >= 0;
        state = State.NEWLINE_AND_INDENT;
        return this;
      }

      // indent only non-whitespace started strings
      indentIfNeeded();
    } else {
      state = State.NONE;
    }

    writer.append(ch);

    switch (ch) {
      case '\n':
        state = State.INDENT;
        break;
      case '{':
        writer.append('\n');
        state = State.INDENT;
        ++indentationLevel;
        break;
      case ';':
        // each semicolon will be followed by the newline
        writer.append('\n');
        state = State.INDENT;
        break;
    }

    return this;
  }

  //
  // Private
  //

  private void indentIfNeeded() throws IOException {
    switch (state) {
      case NONE: return;

      case NEWLINE_AND_INDENT:
        writer.append('\n');
        // fallthrough
      case INDENT:
        assert indentationLevel >= 0;
        for (int i = 0; i < indentationLevel; ++i) {
          writer.append(indentUnit);
        }
        state = State.NONE;
        return;

      default:
        throw new IllegalStateException("Unrecognized state=" + state); // shouldn't happen
    }
  }
}
