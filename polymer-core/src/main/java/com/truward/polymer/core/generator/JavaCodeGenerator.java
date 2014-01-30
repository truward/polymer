package com.truward.polymer.core.generator;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.generator.model.CodeObjectPrinter;
import com.truward.polymer.core.generator.model.SingleLineComment;
import com.truward.polymer.core.generator.support.IndentationAwarePrinter;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class JavaCodeGenerator {
  private final List<Object> elements;
  private int importInsertIndex;
  private JavaTypeRefManager typeRefManager = new JavaTypeRefManager();


  public JavaCodeGenerator(int initialSize) {
    elements = new ArrayList<>(initialSize);
    clear();
  }

  public JavaCodeGenerator() {
    this(1000);
  }

  public void clear() {
    elements.clear();
    importInsertIndex = -1;
    typeRefManager.clear();
  }

  public void printContents(@Nonnull PrintStream out) {
    // now we need to generate imports
    assert importInsertIndex > 0;
    final List<String> imports = typeRefManager.getImportNames();
    int insertIndex = importInsertIndex;
    for (final String importedEntity : imports) {
      elements.add(insertIndex++, ImmutableList.of("import", ' ', importedEntity, ';'));
    }
    elements.add(insertIndex, '\n');

    // print elements
    final CodeObjectPrinter printer = new IndentationAwarePrinter(out);
    for (final Object o : elements) {
      printer.print(o);
    }
  }

  public void packageDirective(FqName packageName) {
    if (importInsertIndex >= 0) {
      throw new IllegalStateException("Generating package name multiple times does not make sense");
    }

    text("package").ch(' ');
    for (boolean next = false;;packageName = packageName.getParent(), next = true) {
      if (next) {
        ch('.');
      }
      text(packageName.getName());
      if (packageName.isRoot()) {
        break;
      }
    }
    ch(';', '\n');
    importInsertIndex = elements.size();
  }

  public JavaCodeGenerator text(String text) {
    elements.add(text);
    return this;
  }

  public JavaCodeGenerator textWithSpaces(String... lexemas) {
    for (int i = 0; i < lexemas.length; ++i) {
      if (i > 0) {
        elements.add(' ');
      }
      text(lexemas[i]);
    }
    return this;
  }

  public JavaCodeGenerator ch(char c) {
    elements.add(c);
    return this;
  }

  public JavaCodeGenerator ch(char c1, char c2) {
    return ch(c1).ch(c2);
  }

  public JavaCodeGenerator ch(char... chars) {
    for (final char c : chars) {
      ch(c);
    }
    return this;
  }

  public JavaCodeGenerator type(Type type) {
    elements.add(typeRefManager.adaptType(type));
    return this;
  }

  // new {type}
  public JavaCodeGenerator newType(Type type) {
    text("new").ch(' ').type(type);
    return this;
  }

  public JavaCodeGenerator typedVar(Type type, String name) {
    return type(type).ch(' ').text(name);
  }

  public JavaCodeGenerator annotate(Class<?> annotationClass) {
    return ch('@').type(annotationClass).ch('\n');
  }

  // Generates member of some simple expression, i.e. varName.memberName
  public JavaCodeGenerator member(String varName, String memberName) {
    return text(varName).ch('.').text(memberName);
  }

  // Generates ["this", '.', member] sequence
  public JavaCodeGenerator thisMember(String memberName) {
    return member("this", memberName);
  }

  public JavaCodeGenerator singleLineComment(String... lines) {
    elements.add(new SingleLineComment(ImmutableList.<Object>copyOf(lines)));
    return this;
  }

  // Generates space-separated expressions
  public JavaCodeGenerator text(String expr1, String... exprs) {
    text(expr1);
    for (final String expr : exprs) {
      ch(' ').text(expr);
    }
    return this;
  }

  // Generates dotted access, e.g. ['.', field]
  public JavaCodeGenerator dot(String member) {
    return ch('.').text(member);
  }

  // Generates cast expression with space at the end
  public JavaCodeGenerator cast(Class<?> clazz) {
    return ch('(').type(clazz).ch(')', ' ');
  }

  // Generates spaced text, i.e. [' ', expr, ' ']
  public JavaCodeGenerator spText(String expr) {
    return ch(' ').text(expr).ch(' ');
  }
}
