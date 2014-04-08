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
import com.truward.polymer.printer.CAlikePrinter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCodePrinter implements CodePrinter {
  private final CAlikePrinter printer;
  private final TypeManager typeManager;

  public DefaultCodePrinter(@Nonnull Writer writer, @Nonnull String indentUnit, @Nonnull TypeManager typeManager) {
    this.printer = new CAlikePrinter(writer, indentUnit);
    this.typeManager = typeManager;
  }

  public DefaultCodePrinter(@Nonnull Writer writer, @Nonnull TypeManager typeManager) {
    this(writer, CAlikePrinter.DEFAULT_INDENT_UNIT, typeManager);
  }

  public DefaultCodePrinter(@Nonnull Writer writer) {
    this(writer, StubTypeManager.INSTANCE);
  }

  @Override
  public void print(@Nonnull GenObject object) throws IOException {
    GenObjectVisitor.apply(object, new GenObjectVisitor<IOException>() {
      @Override
      public void visitChar(@Nonnull GenChar o) throws IOException {
        printer.print(o.getChar());
      }

      @Override
      public void visitString(@Nonnull GenString o) throws IOException {
        printer.print(o.getString());
      }

      @Override
      public void visitFqNamed(@Nonnull GenFqNamed o) throws IOException {
        printer.print(o.getFqName());
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
        printer.print('[');
        printer.print(']');
      }

      @Override
      public void visitClass(@Nonnull GenClass o) throws IOException {
        if (typeManager.isFqNameRequired(o)) {
          printer.print(o.getFqName());
        } else {
          printer.print(o.getFqName().getName());
        }
      }

      @Override
      public void visitParameterizedType(@Nonnull GenParameterizedType o) throws IOException {
        print(o.getRawType());
        printer.print('<');
        boolean next = false;
        for (final GenType arg : o.getTypeParameters()) {
          if (next) {
            printer.print(',');
          } else {
            next = true;
          }
          print(arg);
        }
        printer.print('>');
      }
    });
  }
}
