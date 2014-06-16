package com.truward.polymer.code.printer;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.code.Jst;
import static com.truward.polymer.code.JstFlag.*;

import com.truward.polymer.code.Operator;
import com.truward.polymer.code.factory.DefaultJstFactory;
import com.truward.polymer.code.factory.JstFactory;
import com.truward.polymer.code.util.JstFactorySupport;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.MemOutputStreamProvider;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link JstPrinter}.
 *
 * @author Alexander Shabanov
 */
public final class JstPrinterTest extends JstFactorySupport {
  private MemOutputStreamProvider mosp;
  private JstPrinter printer;
  private JstFactory factory;
  private Jst.Unit unit;

  @Nonnull @Override protected JstFactory getFactory() {
    return factory;
  }

  @Before
  public void init() {
    mosp = new MemOutputStreamProvider();
    printer = new JstPrinter(mosp);
    factory = new DefaultJstFactory();
    unit = null;
  }

  @Test
  public void shouldPrintClassDecl() throws IOException {
    // Given:
    unit("my.pkg", classDecl(flags(), annotations(), "FooClass"));

    // When:
    printer.print(unit);

    // Then:
    assertSameGeneratedContent("package my.pkg;\n\n\nclass FooClass {\n}", "my/pkg/FooClass.java");
  }

  @Test
  public void shouldPrintClassWithProperties() throws IOException {
    // Given:
    unit("my.pkg", classDecl(flags(STRICTFP), annotations(), "FooClass", null, types(),
        statements(
            var(flags(PRIVATE, FINAL), "a", String.class),
            var(flags(PRIVATE, FINAL), "b", int.class)
        )));

    // When:
    printer.print(unit);
    final String contents = mosp.getContentMap().get("my/pkg/FooClass.java");

    // Then:
    assertTrue(contents.contains("strictfp class FooClass {\n"));
    assertTrue(contents.contains("private final String a;\n"));
    assertTrue(contents.contains("private final int b;\n"));
  }

  @Test
  public void shouldPrintClassWithOverriddenMethods() throws IOException {
    // Given:
    unit("domain", classDecl(flags(), annotations(), "User", null, types(),
        statements(
            // >> public static final String DEFAULT_NAME = "default-name";
            var(flags(PUBLIC, STATIC, FINAL), "DEFAULT_NAME", String.class, literal("default-name")),

            // >> private String name = DEFAULT_NAME;
            var(flags(PRIVATE), "name", String.class, ident("DEFAULT_NAME")),

            // >> public final String getName() { return this.name; }
            method(flags(PUBLIC, FINAL), "getName", String.class, args(), block(returns(ident("this", "name")))),

            // >> @Override public String toString() { return this.name; }
            method(flags(PUBLIC), annotations(override()), "toString", String.class, args(),
                block(
                    // String result
                    var(flags(FINAL), "result", String.class, ident("this", "name")),
                    returns(ident("result")))),

            // >> @Override public int hashCode() { return 1; }
            method(flags(PUBLIC), annotations(override()), "hashCode", int.class, args(),
                block(returns(literal(1)))),

            // >> @Override public boolean equals(Object other) { return false; }
            method(flags(PUBLIC), annotations(override()), "equals", boolean.class, args(var("other", Object.class)),
                block(returns(literal(false))))
        )));

    // When:
    printer.print(unit);
    final String contents = mosp.getContentMap().get("domain/User.java");

    // Then:
    assertTrue(contents.contains("package domain;\n"));
    assertTrue(contents.contains("\nclass User {\n"));
    assertTrue(contents.contains("private String name = DEFAULT_NAME;\n"));
    assertTrue(contents.contains("@Override\n  public String toString() {\n" +
        "    final String result = this.name;\n" +
        "    return result;\n" +
        "  }\n"));
  }

  @Test
  public void shouldPrintSuperclassAndInterfacesForClass() throws IOException {
    // Given:
    unit("my", classDecl(flags(PUBLIC, FINAL), annotations(annotation(Generated.class)), "MyException",
        type(Exception.class), types(type(Serializable.class), type(Cloneable.class)),
        statements()));

    // When:
    printer.print(unit);
    final String contents = mosp.getContentMap().get("my/MyException.java");

    // Then:
    assertEquals("package my;\n\n\n" +
        "@javax.annotation.Generated\n" +
        "public final class MyException extends Exception implements java.io.Serializable, Cloneable {\n" +
        "}",
        contents);
  }

  @Test
  public void shouldPrintExtendInterfacesForInterface() throws IOException {
    // Given:
    unit("my", classDecl(flags(PUBLIC, INTERFACE), annotations(), "IDomainObject",
        null, types(type(Serializable.class), type(Cloneable.class)),
        statements()));

    // When:
    printer.print(unit);
    final String contents = mosp.getContentMap().get("my/IDomainObject.java");

    // Then:
    assertEquals("package my;\n\n\n" +
        "public interface IDomainObject extends java.io.Serializable, Cloneable {\n" +
        "}",
        contents);
  }

  @Test
  public void shouldPrintConditionals() throws IOException {
    // Given:
    unit("my", classDecl(flags(PUBLIC, ABSTRACT), annotations(), "User", null, types(),
        statements(
            // >> public int foo(int a) { if (hashCode() > a) { return 1; } else { return 2; } }
            method(flags(PUBLIC), annotations(), "foo", int.class, args(var("a", int.class)),
                block(
                    ifs(op(Operator.GT, call("hashCode", expressions()), ident("a")),
                        block(returns(literal(1))), block(returns(literal(2))))
                ))
        )));

    // When:
    printer.print(unit);
    final String contents = mosp.getContentMap().get("my/User.java");

    // Then:
    assertEquals("package my;\n\n\n" +
        "public abstract class User {\n\n\n" +
        "  @Override\n" +
        "  public int hashCode() {\n" +
        "    if (hashCode() > a) {\n" +
        "      return 1;\n" +
        "    } else {\n" +
        "      return 2;\n" +
        "    }\n" +
        "  }\n" +
        "}",
        contents);
  }

//
//  @Test
//  public void shouldPrintConditionals() throws IOException {
//    final Ast.ClassDecl userClass = unit(FqName.valueOf("domain.User"));
//
//    final Ast.MethodDecl methodDecl = userClass.addMethodDecl("foo")
//        .addArgument("a", classRef(int.class))
//        .addModifiers(Modifier.PUBLIC)
//        .setReturnType(classRef(int.class));
//
//    methodDecl
//        .addBodyStmt(exprStmt(call("hashCode").setBase(ident("this"))))
//        .addBodyStmt(ifStmt(binary(Operator.GT, ident("a"), literal(0)),
//            returnStmt(literal(1)), // < then
//            returnStmt(literal(2))));
//
//    astPrinter.print(userClass);
//    final String contents = mosp.getContentMap().get("domain/User.java");
//
//    assertTrue(contents.contains("this.hashCode();\n"));
//    assertTrue(contents.contains("if (a > 0) {\n"));
//  }

  //
  // Private
  //

  private void unit(@Nonnull String packageName, @Nonnull Jst.ClassDeclaration clazz) {
    unit = factory.jstUnit(FqName.valueOf(packageName));
    unit.setClasses(ImmutableList.of(clazz));
  }

  private void assertSameGeneratedContent(@Nonnull String expected, @Nonnull String fileName) {
    assertTrue("No " + fileName, mosp.getContentMap().containsKey(fileName));
    final String actualContent = mosp.getContentMap().get(fileName);
    assertEquals(expected, actualContent);
  }
}
