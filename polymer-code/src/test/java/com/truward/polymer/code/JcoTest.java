package com.truward.polymer.code;

import com.truward.polymer.code.naming.FqName;
import org.junit.Test;

/**
 * @author Alexander Shabanov
 */
public final class JcoTest {

  @Test
  public void shouldSerializeCodeDom() {
    final Jco.ClassModule m = new Jco.ClassModule();
    m.setPackageName(FqName.parse("com.mysite"));

    final Jco.ClassDecl cd = new Jco.ClassDecl();
    cd.setParent(m);
    cd.setName("Foo");
    cd.setTypeExpr(new Jco.ClassDeclRef(cd)); // always a reference to itself
    m.addClassDecls(cd);

    final JcoPrinter p = new JcoPrinter(System.out);
    p.print(m);
  }
}
