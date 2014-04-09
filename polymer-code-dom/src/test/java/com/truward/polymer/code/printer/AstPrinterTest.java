package com.truward.polymer.code.printer;

import com.truward.polymer.code.Ast;
import com.truward.polymer.code.DefaultAstFactory;
import com.truward.polymer.code.factory.AstFactory;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.MemOutputStreamProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public final class AstPrinterTest {
  private MemOutputStreamProvider mosp;
  private AstPrinter astPrinter;
  private AstFactory astFactory;

  @Before
  public void init() {
    mosp = new MemOutputStreamProvider();
    astPrinter = new AstPrinter(mosp);
    astFactory = new DefaultAstFactory();
  }

  @Test
  public void shouldPrintClassDecl() {
    final Ast.ClassDecl classDecl = astFactory.classDecl(FqName.parse("my.pkg.FooClass"));
    astPrinter.print(classDecl);
    assertSameGeneratedContent("package my.pkg;\n\n\nclass FooClass {\n}\n", "my/pkg/FooClass.java");
  }

  //
  // Private
  //

  private void assertSameGeneratedContent(String expected, String fileName) {
    assertTrue("No " + fileName, mosp.getContentMap().containsKey(fileName));
    assertEquals(expected, mosp.getContentMap().get(fileName));
  }
}
