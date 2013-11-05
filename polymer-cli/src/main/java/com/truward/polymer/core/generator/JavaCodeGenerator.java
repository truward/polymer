package com.truward.polymer.core.generator;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.generator.model.ClassRef;
import com.truward.polymer.core.generator.model.CodeObjectVisitor;
import com.truward.polymer.core.generator.support.IndentationAwarePrinter;

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

  public void printContents() {
    // now we need to generate imports
    assert importInsertIndex > 0;
    final List<String> imports = getImportNames();
    int insertIndex = importInsertIndex;
    for (final String importedEntity : imports) {
      elements.add(insertIndex++, ImmutableList.of("import", ' ', importedEntity, ';'));
    }
    elements.add(insertIndex, '\n');

    // print elements
    final IndentationAwarePrinter visitor = new IndentationAwarePrinter(System.out);
    for (final Object o : elements) {
      CodeObjectVisitor.apply(visitor, o);
    }
  }

  public void packageDirective(String packageName) {
    text("package").ch(' ').text(packageName).ch(';', '\n');
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

  public JavaCodeGenerator type(Class<?> clazz) {
    ClassRef classRef = classToStub.get(clazz);
    if (classRef == null) {
      classRef = new ClassRef(clazz);
      classToStub.put(clazz, classRef);
    }
    elements.add(classRef);

    return this;
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
