package com.truward.polymer.code.printer;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.code.Jst;
import com.truward.polymer.code.factory.JstFactory;
import com.truward.polymer.code.JstFlag;
import com.truward.polymer.code.factory.DefaultJstFactory;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.MemOutputStreamProvider;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link JstPrinter}.
 *
 * @author Alexander Shabanov
 */
public final class JstPrinterTest {
  private MemOutputStreamProvider mosp;
  private JstPrinter printer;
  private JstFactory factory;
  private Jst.Unit unit;


  @Before
  public void init() {
    mosp = new MemOutputStreamProvider();
    printer = new JstPrinter(mosp);
    factory = new DefaultJstFactory();
    unit = null;
  }

  @Test
  public void shouldPrintClassDecl() throws IOException {
    classDecl("my.pkg", "FooClass");

    printer.print(unit);
    assertSameGeneratedContent("package my.pkg;\n\n\nclass FooClass {\n}", "my/pkg/FooClass.java");
  }

  @Test
  public void shouldPrintClassWithProperties() throws IOException {
    final Jst.ClassDeclaration decl = classDecl("my.pkg", "FooClass");

    decl.setFlags(ImmutableList.of(JstFlag.STRICTFP));

    addField(decl, "a", String.class, JstFlag.PRIVATE, JstFlag.FINAL);
    addField(decl, "b", int.class, JstFlag.PRIVATE, JstFlag.FINAL);

    printer.print(unit);
    final String contents = mosp.getContentMap().get("my/pkg/FooClass.java");

    assertTrue(contents.contains("strictfp class FooClass {\n"));
    assertTrue(contents.contains("private final String a;\n"));
    assertTrue(contents.contains("private final int b;\n"));
  }

  //
  // Private
  //

  @Nonnull private Jst.ClassDeclaration classDecl(@Nonnull String packageName, @Nonnull String className) {
    final Jst.ClassDeclaration decl = factory.jstClass(className);
    unit = factory.jstUnit(FqName.valueOf(packageName));
    unit.setClasses(ImmutableList.of(decl));
    return decl;
  }

  @Nonnull private Jst.VarDeclaration addField(@Nonnull Jst.ClassDeclaration decl,
                                               @Nonnull String fieldName,
                                               @Nonnull Class<?> fieldType,
                                               @Nonnull JstFlag... modifiers) {
    final Jst.VarDeclaration var = factory.jstVar(fieldName, factory.jstClassType(fieldType));
    var.setFlags(ImmutableList.copyOf(modifiers));
    addStatement(decl.getBody(), var);
    return var;
  }

  private void addStatement(@Nonnull Jst.Block block, @Nonnull Jst.Statement statement) {
    final List<Jst.Statement> statements = new ArrayList<>(block.getStatements().size() + 1);
    statements.addAll(block.getStatements());
    statements.add(statement);
    block.setStatements(statements);
  }

  private void assertSameGeneratedContent(@Nonnull String expected, @Nonnull String fileName) {
    assertTrue("No " + fileName, mosp.getContentMap().containsKey(fileName));
    final String actualContent = mosp.getContentMap().get(fileName);
    assertEquals(expected, actualContent);
  }
}
