package com.truward.polymer.core.code.printer;

import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.support.code.DefaultInlineBlock;
import com.truward.polymer.core.support.code.printer.DefaultCodePrinter;
import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests code printer
 *
 * @author Alexander Shabanov
 */
public final class CodePrinterTest {
  private CodePrinter codePrinter;
  private StringWriter stringWriter;
  private GenInlineBlock codeStream;

  @Before
  public void init() {
    stringWriter = new StringWriter(1000);
    codePrinter = new DefaultCodePrinter(stringWriter);
    codeStream = new DefaultInlineBlock();
  }

  @Test
  public void shouldWritePackage() throws IOException {
    codeStream.s("package").sp().s(FqName.valueOf("com.test")).c(';');
    codePrinter.print(codeStream);
    assertEquals("package com.test;\n", stringWriter.toString());
  }

  @Test
  public void shouldGenerateCode() throws IOException {
    codeStream.s("public").c(' ').s("class").sp().s("Foo").c(' ', '{');

    codeStream.s("public").sp().s("static").sp().s("void").sp().s("main").c('(').s("String[] args").c(')', ' ', '{')
        .s("// sample comment").eol()
        .c('}')
        .c('}');

    codePrinter.print(codeStream);
    final String content = stringWriter.toString();

    // verify content
    assertTrue("Should contain class definition", content.contains("public class Foo {\n"));
    assertTrue("Should contain function definition", content.contains("public static void main(String[] args) {\n"));
    assertTrue("Should contain closing brace", content.contains("}\n"));
  }
}
