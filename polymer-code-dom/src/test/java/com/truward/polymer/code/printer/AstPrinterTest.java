package com.truward.polymer.code.printer;

import com.truward.polymer.code.Ast;
import com.truward.polymer.code.DefaultAstFactory;
import com.truward.polymer.code.factory.AstFactory;
import com.truward.polymer.code.factory.DelegatingAstFactory;
import com.truward.polymer.code.util.AstUtil;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.MemOutputStreamProvider;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

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
    final Ast.ClassDecl classDecl = classDecl(FqName.valueOf("my.pkg.FooClass")).addModifiers(Modifier.STRICTFP);
    classDecl.addField("a", classRef(String.class)).addModifiers(AstUtil.PRIVATE_FINAL);
    classDecl.addField("b", classRef(int.class)).addModifiers(AstUtil.PRIVATE_FINAL);
    astPrinter.print(classDecl);
    final String contents = mosp.getContentMap().get("my/pkg/FooClass.java");

    assertTrue(contents.contains("strictfp class FooClass {\n"));
    assertTrue(contents.contains("private final String a;\n"));
    assertTrue(contents.contains("private final int b;\n"));
  }

  @Test
  public void shouldPrintClassWithOverriddenMethods() throws IOException {
    final Ast.ClassDecl userClass = classDecl(FqName.valueOf("domain.User"));

    userClass.addField("name", classRef(String.class)).addModifiers(Modifier.PRIVATE);

    // >> public final String getName() { return this.name; }
    userClass.addMethodDecl("getName").addModifiers(AstUtil.PUBLIC_FINAL)
        .addBodyStmt(returnStmt(select(ident("this"), "name")))
        .setReturnType(classRef(String.class));

    // >> @Override public String toString() { return this.name; }
    userClass.addMethodDecl("toString").addModifiers(Modifier.PUBLIC).addAnnotation(annotation(Override.class))
        .addBodyStmt(var("result", classRef(String.class))
            .setInitializer(select(ident("this"), "name")))
        .addBodyStmt(returnStmt(ident("result")))
        .setReturnType(classRef(String.class));

    // >> @Override public int hashCode() { return 1; }
    AstUtil.makePublicOverride(this, userClass.addMethodDecl("hashCode"))
        .addBodyStmt(returnStmt(literal(1)))
        .setReturnType(classRef(int.class));

    // >> @Override public boolean equals(Object other) { return false; }
    AstUtil.makePublicOverride(this, userClass.addMethodDecl("equals"))
        .addArgument("other", classRef(Object.class))
        .addBodyStmt(returnStmt(literal(false)))
        .setReturnType(classRef(boolean.class));

    // print class and get contents
    astPrinter.print(userClass);
    final String contents = mosp.getContentMap().get("domain/User.java");

    assertTrue(contents.contains("private String name;\n"));
    assertTrue(contents.contains("public String toString() {\n"));
    assertTrue(contents.contains("String result = this.name;\n"));
    assertTrue(contents.contains("return result;\n"));
    assertTrue(contents.contains("public boolean equals(Object other) {\n"));
  }

  @Test
  public void shouldPrintSuperclassAndInterfaces() throws IOException {
    final Ast.ClassDecl userClass = classDecl(FqName.valueOf("domain.User"))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addInterface(classRef(Serializable.class))
        .addInterface(classRef(Cloneable.class))
        .setSuperclass(classRef(Date.class));

    // print class and get contents
    astPrinter.print(userClass);
    final String contents = mosp.getContentMap().get("domain/User.java");

    assertTrue(contents.contains("public abstract class User extends java.util.Date " +
        "implements java.io.Serializable, Cloneable {\n"));
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
