package com.truward.polymer.code.printer;

import com.truward.polymer.code.Ast;
import com.truward.polymer.code.DefaultAstFactory;
import com.truward.polymer.code.factory.AstFactory;
import com.truward.polymer.code.factory.DelegatingAstFactory;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.MemOutputStreamProvider;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public final class AstPrinterTest extends DelegatingAstFactory {
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
  public void shouldPrintClassDecl() throws IOException {
    final Ast.ClassDecl classDecl = classDecl(FqName.valueOf("my.pkg.FooClass"));
    astPrinter.print(classDecl);
    assertSameGeneratedContent("package my.pkg;\n\n\nclass FooClass {\n}\n", "my/pkg/FooClass.java");
  }

  @Test
  public void shouldPrintClassWithProperties() throws IOException {
    final Ast.ClassDecl classDecl = classDecl(FqName.valueOf("my.pkg.FooClass"));
    classDecl.addField("a", classRef(String.class));
    classDecl.addField("b", classRef(int.class));
    astPrinter.print(classDecl);
    System.out.println(mosp.getContentMap().get("my/pkg/FooClass.java"));
  }

  //
  // Private
  //


  @Nonnull @Override protected AstFactory getDelegate() {
    return astFactory;
  }

  private void assertSameGeneratedContent(String expected, String fileName) {
    assertTrue("No " + fileName, mosp.getContentMap().containsKey(fileName));
    final String actualContent = mosp.getContentMap().get(fileName);
    assertEquals(expected, actualContent);
  }
}
