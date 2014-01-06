package com.truward.polymer.core.generator;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.generator.model.CodeObject;
import com.truward.polymer.core.generator.model.CodeObjectPrinter;
import com.truward.polymer.core.generator.model.Printable;
import com.truward.polymer.core.generator.model.Text;
import com.truward.polymer.code.TypeVisitor;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Takes care of java types
 *
 * @author Alexander Shabanov
 */
public class JavaTypeRefManager {
  private final Map<Class<?>, ClassRef> classRefs = new HashMap<>(100);

  public void clear() {
    classRefs.clear();
  }

  @Nonnull
  public CodeObject adaptType(@Nonnull Type type) {
    return TypeVisitor.apply(new TypeVisitor<CodeObject>() {
      @Override
      public CodeObject visitArray(@Nonnull Type sourceType, @Nonnull Class<?> elementType) {
        return new ArrayRef(adaptType(elementType));
      }

      @Override
      public CodeObject visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
        // non-array type
        ClassRef classRef = classRefs.get(clazz);
        if (classRef == null) {
          classRef = new ClassRef(clazz);
          classRefs.put(clazz, classRef);
        }
        return classRef;
      }

      @Override
      public CodeObject visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
        final CodeObject rawTypeCodeObject = adaptType(rawType);

        final CodeObject[] codeObjArgs = new CodeObject[args.size()];
        for (int i = 0; i < codeObjArgs.length; ++i) {
          codeObjArgs[i] = adaptType(args.get(i));
        }

        return new ParameterizedTypeRef(rawTypeCodeObject, ImmutableList.copyOf(codeObjArgs));
      }
    }, type);
  }

  @Nonnull
  public List<String> getImportNames() {
    final Map<String, ClassRef> simpleNameToRef = new HashMap<>(classRefs.size());

    // add those referenced classes that are visible by default
    for (final ClassRef classRef : classRefs.values()) {
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

    // add all the other classes that might not be visible
    for (final ClassRef classRef : classRefs.values()) {
      if (classRef.isVisibleByDefault()) {
        continue;
      }

      final String simpleName = classRef.getSimpleName();
      if (simpleNameToRef.containsKey(simpleName)) {
        classRef.setSimpleNameEnabled(false);
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

  //
  // Private
  //

  private static final class ArrayRef implements Printable {
    private final CodeObject element;

    private ArrayRef(CodeObject element) {
      this.element = element;
    }

    @Override
    public void print(@Nonnull CodeObjectPrinter out) {
      out.print(element);
      out.print('[');
      out.print(']');
    }
  }

  private static final class ParameterizedTypeRef implements Printable {
    private final CodeObject rawType;
    private final List<CodeObject> arguments;

    private ParameterizedTypeRef(@Nonnull CodeObject rawType, @Nonnull List<CodeObject> arguments) {
      this.rawType = rawType;
      this.arguments = arguments;
    }

    @Override
    public void print(@Nonnull CodeObjectPrinter printer) {
      printer.print(rawType);
      printer.print('<');
      boolean next = false;
      for (final CodeObject arg : arguments) {
        if (next) {
          printer.print(", ");
        } else {
          next = true;
        }
        printer.print(arg);
      }
      printer.print('>');
    }
  }

  private static final class ClassRef implements Text {
    private final Class<?> originClass;
    private Boolean simpleNameEnabled;

    public ClassRef(@Nonnull Class<?> originClass) {
      this.originClass = originClass;
      assert !originClass.isArray();
      if (isVisibleByDefault()) {
        this.simpleNameEnabled = true;
      }
    }

    public Class<?> getOriginClass() {
      return originClass;
    }

    public String getSimpleName() {
      return getOriginClass().getSimpleName();
    }

    public String getQualifiedName() {
      return getOriginClass().getCanonicalName();
    }

    public boolean isVisibleByDefault() {
      return originClass.isPrimitive() || originClass.getPackage().getName().equals("java.lang");
    }

    public boolean isSimpleNameEnabled() {
      if (this.simpleNameEnabled == null) {
        throw new IllegalStateException("It is unknown whether simple name is enabled or not");
      }
      return simpleNameEnabled;
    }

    public void setSimpleNameEnabled(boolean simpleNameEnabled) {
      if (this.simpleNameEnabled != null) {
        throw new IllegalStateException("Unable to set name twice");
      }
      this.simpleNameEnabled = simpleNameEnabled;
    }

    @Override
    public String getText() {
      if (isSimpleNameEnabled()) {
        return originClass.getSimpleName();
      }

      return originClass.getCanonicalName();
    }
  }
}
