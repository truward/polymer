package com.truward.polymer.core.generator;

import org.junit.Test;

/**
 * @author Alexander Shabanov
 */
public class JavaCodeGeneratorTest {

  @Test
  public void shouldGenerateCode() {
    final JavaCodeGenerator g = new JavaCodeGenerator();

    g.packageDirective("com.mysite.product");
    g.text("public").ch(' ').text("class").ch(' ').text("Foo").ch(' ', '{');

    g.textWithSpaces("public", "static", "void", "main").ch('(').text("String[] args").ch(')').ch(' ', '{');
    g.textWithSpaces("return", "0").ch(';');
    g.ch('}');

    g.ch('}');

    g.printContents();
  }
}
