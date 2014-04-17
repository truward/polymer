package com.truward.polymer.code;

import com.truward.polymer.code.factory.AstFactory;
import com.truward.polymer.code.factory.DelegatingAstFactory;
import com.truward.polymer.code.util.AstUtil;
import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AstFactoryTest extends DelegatingAstFactory {
  private AstFactory factory;

  @Before
  public void init() {
    factory = new DefaultAstFactory();
  }

  @Test
  public void shouldCreateClassWithMethod() {
    final Ast.ClassDecl myClass = classDecl(FqName.valueOf("com.mysite.MyClass"));
    myClass.addModifiers(AstUtil.PUBLIC_FINAL);
    final Ast.MethodDecl fooMethod = myClass.addMethodDecl("foo")
        .addAnnotation(annotation(Override.class))
        .addArgument("str", classRef(String.class))
        .addBodyStmt(returnStmt(literal("ToString")))
        .setReturnType(voidType());
    assertTrue(fooMethod.getAnnotations().size() > 0);
  }

  //
  // Private
  //

  @Nonnull @Override protected AstFactory getDelegate() {
    assertNotNull(factory);
    return factory;
  }
}
