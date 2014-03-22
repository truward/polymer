package com.truward.polymer.core.support.code;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.typed.*;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Alexander Shabanov
 */
public final class DefaultTypeManager extends FreezableSupport implements TypeManager {
  private static final FqName JAVA_LANG = FqName.parse("java.lang");

  private static final int DEFAULT_INITIAL_SIZE = 50;

  private final Set<GenClass> genClasses = new HashSet<>(DEFAULT_INITIAL_SIZE);
  private final Map<Class<?>, GenClass> classToGen = new HashMap<>(DEFAULT_INITIAL_SIZE);
  private FqName currentPackage;
  private Set<GenClass> classesWithSimpleNames;


  @Override
  public void start() {
    melt();
    currentPackage = null;
    genClasses.clear();
    classToGen.clear();
  }

  @Override
  public void setPackageName(@Nonnull FqName currentPackage) {
    checkNonFrozen();
    this.currentPackage = currentPackage;
  }

  @Nonnull
  @Override
  public GenType adaptType(@Nonnull Type type) {
    checkNonFrozen();

    return TypeVisitor.apply(new TypeVisitor<GenType>() {
      @Override
      public GenType visitArray(@Nonnull Type sourceType, @Nonnull Type elementType) {
        return new GenArrayImpl(adaptType(elementType));
      }

      @Override
      public GenType visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
        // non-array typed
        GenClass genClass = classToGen.get(clazz);
        if (genClass == null) {
          genClass = new JavaGenClass(clazz);
          classToGen.put(clazz, genClass);
          genClasses.add(genClass);
        }
        return genClass;
      }

      @Override
      public GenType visitGenericType(@Nonnull Type sourceType, @Nonnull Type rawType, @Nonnull List<? extends Type> args) {
        final GenType genRawType = adaptType(rawType);

        final GenType[] genArgs = new GenType[args.size()];
        for (int i = 0; i < genArgs.length; ++i) {
          genArgs[i] = adaptType(args.get(i));
        }

        return new GenParameterizedTypeImpl(genRawType, ImmutableList.copyOf(genArgs));
      }

      @Override
      public GenType visitGenClass(@Nonnull Type sourceType, @Nonnull GenClass ref) {
        genClasses.add(ref);
        return ref;
      }
    }, type);
  }

  @Override
  protected void setFrozen() {
    cannotBeFrozenIf(currentPackage == null, "Current package is not initialized");

    final Map<String, GenClass> simpleNameToRef = new HashMap<>(genClasses.size() * 2);

    // add those referenced classes that are visible by default
    for (final GenClass genClass : genClasses) {
      final String simpleName = genClass.getFqName().getName();
      if (!isVisibleByDefault(genClass)) {
        continue;
      }

      if (simpleNameToRef.containsKey(simpleName)) {
        // theoretically can't happen
        throw new IllegalStateException("Expectation failed: primitive builtin type duplicated");
      }

      simpleNameToRef.put(simpleName, genClass);
    }

    // add all the other classes that might not be visible
    for (final GenClass genClass : genClasses) {
      if (isVisibleByDefault(genClass)) {
        continue;
      }

      final String simpleName = genClass.getFqName().getName();
      if (simpleNameToRef.containsKey(simpleName)) {
        continue;
      }

      simpleNameToRef.put(simpleName, genClass);
    }

    classesWithSimpleNames = ImmutableSet.copyOf(simpleNameToRef.values());

    super.setFrozen();
  }

  @Override
  @Nonnull
  public List<FqName> getImportNames() {
    checkIsFrozen();

    final Set<FqName> importStatements = new TreeSet<>();
    for (final GenClass genClass : genClasses) {
      if (isVisibleByDefault(genClass) || !classesWithSimpleNames.contains(genClass)) {
        continue;
      }

      importStatements.add(genClass.getFqName());
    }

    return ImmutableList.copyOf(importStatements);
  }

  @Override
  public boolean isFqNameRequired(@Nonnull GenClass genClass) {
    checkIsFrozen();
    return !isVisibleByDefault(genClass) && !classesWithSimpleNames.contains(genClass);

  }

  //
  // Private
  //

  private boolean isVisibleByDefault(GenClass genClass) {
    if (genClass.isPrimitive()) {
      return true; // primitive types are always visible
    }

    final FqName fqName = genClass.getFqName();
    if (fqName.isRoot()) {
      return currentPackage.isRoot();
    }

    final FqName parentFqName = fqName.getParent();
    return parentFqName.equals(JAVA_LANG) || parentFqName.equals(currentPackage);

  }

  private static final class GenArrayImpl implements GenArray {
    private final GenType elementType;

    private GenArrayImpl(@Nonnull GenType elementType) {
      this.elementType = elementType;
    }

    @Override
    @Nonnull
    public GenType getElementType() {
      return elementType;
    }

    @Override
    public String toString() {
      return elementType.toString() + "[]";
    }
  }

  private static final class GenParameterizedTypeImpl implements GenParameterizedType {
    private final GenType rawType;
    private final List<GenType> arguments;

    public GenParameterizedTypeImpl(@Nonnull GenType rawType, @Nonnull List<GenType> arguments) {
      this.rawType = rawType;
      this.arguments = ImmutableList.copyOf(arguments);
    }

    @Nonnull @Override public GenType getRawType() {
      return rawType;
    }

    @Nonnull @Override public List<GenType> getTypeParameters() {
      return arguments;
    }

    @Override
    public String toString() {
      final StringBuilder result = new StringBuilder(100);
      result.append(rawType);
      result.append('<');
      boolean next = false;
      for (final GenType arg : getTypeParameters()) {
        if (next) {
          result.append(", ");
        } else {
          next = true;
        }
        result.append(arg);
      }
      result.append('>');
      return result.toString();
    }
  }

  private static final class JavaGenClass implements GenClass {
    private final Class<?> originClass;
    private transient final FqName targetName;

    public JavaGenClass(@Nonnull Class<?> originClass) {
      assert !originClass.isArray();
      this.originClass = originClass;
      String className = originClass.getName();
      if (className.contains("$")) {
        // inner class
        className = className.replace('$', '.');
      }
      this.targetName = FqName.parse(className);
    }

    @Override
    public void setFqName(@Nonnull FqName name) {
      throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public FqName getFqName() {
      return targetName;
    }

    @Override
    public boolean hasFqName() {
      return true;
    }

    @Override
    public boolean isPrimitive() {
      return originClass.isPrimitive();
    }

    @Override
    public String toString() {
      return "JavaGenClass#" + originClass.toString();
    }
  }
}
