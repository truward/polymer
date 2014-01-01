package com.truward.polymer.core.generator;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.generator.model.ClassRef;
import com.truward.polymer.core.generator.model.CodeObjectVisitor;
import com.truward.polymer.core.generator.support.IndentationAwarePrinter;
import com.truward.polymer.code.naming.FqName;

import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Alexander Shabanov
 */
public final class JavaCodeGenerator {
  private final List<Object> elements;
  private int importInsertIndex;
  private final Map<Class<?>, ClassRef> classToStub = new HashMap<>(100);

  public JavaCodeGenerator(int initialSize) {
    elements = new ArrayList<>(initialSize);
    clear();
  }

  public JavaCodeGenerator() {
    this(1000);
  }

  public void clear() {
    elements.clear();
    classToStub.clear();
    importInsertIndex = -1;
  }

  public void printContents(PrintStream out) {
    // now we need to generate imports
    assert importInsertIndex > 0;
    final List<String> imports = getImportNames();
    int insertIndex = importInsertIndex;
    for (final String importedEntity : imports) {
      elements.add(insertIndex++, ImmutableList.of("import", ' ', importedEntity, ';'));
    }
    elements.add(insertIndex, '\n');

    // print elements
    final IndentationAwarePrinter visitor = new IndentationAwarePrinter(out);
    for (final Object o : elements) {
      CodeObjectVisitor.apply(visitor, o);
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
    if (type instanceof Class) {
      // non-generic type
      final Class<?> clazz = (Class) type;
      ClassRef classRef = classToStub.get(clazz);
      if (classRef == null) {
        classRef = new ClassRef(clazz);
        classToStub.put(clazz, classRef);
      }
      elements.add(classRef);
    } else {
      // TODO: generic types
      throw new UnsupportedOperationException("Unsupported type: " + type);
    }

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

  //
  // Private
  //

  private List<String> getImportNames() {
    final Map<String, ClassRef> simpleNameToRef = new HashMap<>(classToStub.size());

    // add those referenced classes that are visible by default
    for (final ClassRef classRef : classToStub.values()) {
      final String simpleName = classRef.getSimpleName();
      if (!classRef.isVisibleByDefault()) {
        continue;
      }

      if (simpleNameToRef.containsKey(simpleName)) {
        // theoretically can't happen
        throw new IllegalStateException("Expectation failed: primitive builtin type duplicated");
      }

      simpleNameToRef.put(simpleName, classRef);
    }

    // add all the rest classes that might not be visible
    for (final ClassRef classRef : classToStub.values()) {
      if (classRef.isVisibleByDefault()) {
        continue;
      }

      final String simpleName = classRef.getSimpleName();
      if (simpleNameToRef.containsKey(simpleName)) {
        assert !classRef.isSimpleNameEnabled();
        continue;
      }

      classRef.setSimpleNameEnabled(true);
      simpleNameToRef.put(simpleName, classRef);
    }

    final Set<String> importStatements = new TreeSet<>();
    for (final ClassRef classRef : simpleNameToRef.values()) {
      if (classRef.isVisibleByDefault() || !classRef.isSimpleNameEnabled()) {
        continue;
      }
      importStatements.add(classRef.getQualifiedName());
    }

    return ImmutableList.copyOf(importStatements);
  }
}
