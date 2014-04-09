package com.truward.polymer.code;

import com.truward.polymer.code.factory.AstFactory;
import com.truward.polymer.code.factory.DelegatingAstFactory;
import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertNotNull;

public class AstFactoryTest extends DelegatingAstFactory {
  private AstFactory factory;

  @Before
  public void init() {
    factory = new DefaultAstFactory();
  }

  @Test
  public void shouldCreateClassWithMethod() {
    final Ast.ClassDecl myClass = classDecl(FqName.parse("com.mysite.MyClass"));
    myClass.makePublicFinal();
    final Ast.MethodDecl fooMethod = myClass.addMethodDecl("foo");
    fooMethod.setReturnType(voidType());
  }

  //
  // Private
  //


  @Nonnull @Override protected AstFactory getDelegate() {
    assertNotNull(factory);
    return factory;
  }
}
