package com.truward.polymer.output;

import com.truward.polymer.printer.CAlikePrinter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link CAlikePrinter}
 *
 * @author Alexander Shabanov
 */
public final class CAlikePrinterTest {
  private CAlikePrinter p;
  private StringWriter writer;

  @Before
  public void init() {
    writer = new StringWriter(100);
    p = new CAlikePrinter(writer);
  }

  @Test
  public void shouldPrintIndented() throws IOException {
    p.print("static").print(' ').print('{');
    p.print("foo()").print(';');
    p.print("bar()").print(';');
    p.print("if").print(' ').print('a').print(' ').print('{');
    p.print("baz()").print(';');
    p.print('}');
    p.print('}');

    assertEquals("static {\n" +
        "  foo();\n" +
        "  bar();\n" +
        "  if a {\n" +
        "    baz();\n" +
        "  }\n" +
        "}\n", writer.toString());
  }

  @Test
  public void shouldNotIndentWhitespaceAfterCloseBrace() throws IOException {
    p.print('{');
    p.print('a').print(';');
    p.print('}').print(' ').print("else").print(' ').print('{');
    p.print('b').print(';');
    p.print('}');

    assertEquals("{\n" +
        "  a;\n" +
        "} else {\n" +
        "  b;\n" +
        "}\n", writer.toString());
  }
}
