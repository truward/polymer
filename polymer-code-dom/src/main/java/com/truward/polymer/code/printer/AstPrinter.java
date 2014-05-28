package com.truward.polymer.code.printer;

import com.truward.polymer.code.Ast;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.OutputStreamProvider;
import com.truward.polymer.output.StandardFileType;
import com.truward.polymer.printer.CAlikePrinter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Encapsulates printing class declarations into the streams, provided by {@link OutputStreamProvider}.
 *
 * @author Alexander Shabanov
 */
public final class AstPrinter {
  private final OutputStreamProvider provider;

  public AstPrinter(@Nonnull OutputStreamProvider provider) {
    this.provider = provider;
  }

  public void print(@Nonnull Ast.ClassDecl classDecl) throws IOException {
    final FqName className = classDecl.getFqName();
    try (final OutputStream out = provider.createStreamForFile(className, StandardFileType.JAVA)) {
      try (final Writer writer = new OutputStreamWriter(out, OutputStreamProvider.DEFAULT_CHARSET)) {
        final CAlikePrinter printer = new CAlikePrinter(writer);

        final ClassPrinter classPrinter = new ClassPrinter(printer, classDecl);
        classPrinter.print();
      }
    }
  }
}
