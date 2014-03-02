package com.truward.polymer.core.support.code.printer;

import com.truward.polymer.core.code.GenObject;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.printer.CodePrinter;
import com.truward.polymer.core.code.typed.GenArray;
import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenParameterizedType;
import com.truward.polymer.core.code.typed.GenType;
import com.truward.polymer.core.code.untyped.GenChar;
import com.truward.polymer.core.code.untyped.GenFqNamed;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.code.untyped.GenString;
import com.truward.polymer.core.code.visitor.GenObjectVisitor;
import com.truward.polymer.core.support.code.StubTypeManager;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCodePrinter implements CodePrinter {
  public static final String DEFAULT_INDENT_UNIT = "  ";

  private final Writer writer;
  private int indentationLevel = 0;
  private boolean doIndent = true;
  private final String indentUnit;
  private final TypeManager typeManager;

  public DefaultCodePrinter(@Nonnull Writer writer, @Nonnull String indentUnit, @Nonnull TypeManager typeManager) {
    this.writer = writer;
    this.indentUnit = indentUnit;
    this.typeManager = typeManager;
  }

  public DefaultCodePrinter(@Nonnull Writer writer, @Nonnull TypeManager typeManager) {
    this(writer, DEFAULT_INDENT_UNIT, typeManager);
  }

  public DefaultCodePrinter(@Nonnull Writer writer) {
    this(writer, StubTypeManager.INSTANCE);
  }

  @Override
  public void print(@Nonnull GenObject object) throws IOException {
    GenObjectVisitor.apply(object, new GenObjectVisitor<IOException>() {
      @Override
      public void visitChar(@Nonnull GenChar o) throws IOException {
        printChar(o.getChar());
      }

      @Override
      public void visitString(@Nonnull GenString o) throws IOException {
        printText(o.getString());
      }

      @Override
      public void visitFqNamed(@Nonnull GenFqNamed o) throws IOException {
        printFqName(o.getFqName());
      }

      @Override
      public void visitInlineBlock(@Nonnull GenInlineBlock o) throws IOException {
        for (final GenObject child : o.getChilds()) {
          print(child);
        }
      }

      @Override
      public void visitArray(@Nonnull GenArray o) throws IOException {
        print(o.getElementType());
        printChar('[');
        printChar(']');
      }

      @Override
      public void visitClass(@Nonnull GenClass o) throws IOException {
        if (typeManager.isFqNameRequired(o)) {
          printFqName(o.getFqName());
        } else {
          printText(o.getFqName().getName());
        }
      }

      @Override
      public void visitParameterizedType(@Nonnull GenParameterizedType o) throws IOException {
        print(o.getRawType());
        printChar('<');
        boolean next = false;
        for (final GenType arg : o.getTypeParameters()) {
          if (next) {
            printChar(',');
          } else {
            next = true;
          }
          print(arg);
        }
        printChar('>');
      }
    });
  }

  //
  // Private
  //

  private void printText(CharSequence text) throws IOException {
    indentIfNeeded();
    writer.append(text);
  }

  private void printFqName(@Nonnull FqName fqName) throws IOException {
    indentIfNeeded();
    fqName.appendTo(writer);
  }

  // TODO: simplify
  private void printChar(char ch) throws IOException {
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
      return;
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
  }

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
