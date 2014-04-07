package com.truward.polymer.code;

import com.truward.polymer.naming.FqName;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AstFactoryTest {

  @Test
  public void shouldCreateClassWithMethod() {
    final AstFactory f = new AstFactory();
    final Ast.ClassDecl myClass = f.classDecl(FqName.parse("com.mysite.MyClass"));
    myClass.makePublicFinal();
    final Ast.MethodDecl fooMethod = myClass.addMethodDecl("foo");
    fooMethod.setReturnType(f.voidType());
  }
}
