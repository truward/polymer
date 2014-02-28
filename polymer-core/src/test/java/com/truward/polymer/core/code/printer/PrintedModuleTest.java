package com.truward.polymer.core.code.printer;

import com.truward.polymer.core.code.builder.ModuleBuilder;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.support.code.DefaultInlineBlock;
import com.truward.polymer.core.support.code.DefaultModuleBuilder;
import com.truward.polymer.core.support.code.DefaultTypeManager;
import com.truward.polymer.core.support.code.printer.DefaultCodePrinter;
import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

/**
 * Tests module builder with code printer
 *
 * @author Alexander Shabanov
 */
public final class PrintedModuleTest {
  private StringWriter stringWriter;
  private CodePrinter codePrinter;
  private TypeManager typeManager;

  private final FqName packageName = FqName.parse("com.company.product");

  @Before
  public void init() {
    stringWriter = new StringWriter(1000);
    typeManager = new DefaultTypeManager();
    codePrinter = new DefaultCodePrinter(stringWriter, typeManager);
  }

  @Test
  public void shouldPrintModuleContents() throws IOException {
    final ModuleBuilder moduleBuilder = new DefaultModuleBuilder(packageName, typeManager);
    moduleBuilder.getStream()
        .s("public").sp().s("class").sp().s("Foo").sp().s("implements").c(' ').t(Serializable.class).c(' ', '{')
        .s("public").sp().t(String.class).sp().s("toString").c('(', ')', ' ', '{')
        .s("return").sp().s("\"Test\"").c(';')
        .c('}')
        .c('}');

    moduleBuilder.freeze();
    codePrinter.print(moduleBuilder.getStream());

    final String content = stringWriter.toString();
    assertTrue("Should contain Serializable import", content.contains("import java.io.Serializable;\n"));
  }
}
