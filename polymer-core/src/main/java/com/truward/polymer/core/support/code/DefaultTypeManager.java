package com.truward.polymer.core.support.code;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenParameterizedType;
import com.truward.polymer.core.code.typed.GenType;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Alexander Shabanov
 */
public final class DefaultTypeManager implements TypeManager {
  private static final FqName JAVA_LANG = FqName.parse("java.lang");

  private static final int DEFAULT_INITIAL_SIZE = 50;

  private final Set<GenClass> genClasses = new HashSet<>(DEFAULT_INITIAL_SIZE);
  private final Map<Class<?>, GenClass> classToGen = new HashMap<>(DEFAULT_INITIAL_SIZE);
  private FqName currentPackage;


  @Override
  public void start(@Nonnull FqName currentPackage) {
    this.currentPackage = currentPackage;
    genClasses.clear();
    classToGen.clear();
  }

  @Nonnull
  @Override
  public GenType adaptType(@Nonnull Type type) {
    return TypeVisitor.apply(new TypeVisitor<GenType>() {
      @Override
      public GenType visitArray(@Nonnull Type sourceType, @Nonnull Class<?> elementType) {
        return new GenArrayImpl(adaptType(elementType));
      }

      @Override
      public GenType visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
        // non-array typed
        GenClass classRef = classToGen.get(clazz);
        if (classRef == null) {
          classRef = new JavaClassRef(clazz);
          classToGen.put(clazz, classRef);
          genClasses.add(classRef);
        }
        return classRef;
      }

      @Override
      public GenType visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
        final GenType genRawType = adaptType(rawType);

        final GenType[] genArgs = new GenType[args.size()];
        for (int i = 0; i < genArgs.length; ++i) {
          genArgs[i] = adaptType(args.get(i));
        }

        return new GenParameterizedTypeImpl(genRawType, ImmutableList.copyOf(genArgs));
      }

      @Override
      public GenType visitLocalRef(@Nonnull Type sourceType, @Nonnull GenClass ref) {
        genClasses.add(ref);
        return ref;
      }
    }, type);
  }

  @Override
  @Nonnull
  public List<FqName> getImportNames() {
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

    final Set<FqName> importStatements = new TreeSet<>();
    for (final GenClass classRef : genClasses) {
      if (isVisibleByDefault(classRef) || simpleNameToRef.values().contains(classRef)) {
        continue;
      }

      importStatements.add(classRef.getFqName());
    }

    return ImmutableList.copyOf(importStatements);
  }

  //
  // Private
  //

  private boolean isVisibleByDefault(GenClass genClass) {
    final FqName parentFqName = genClass.getFqName().getParent();
    return !parentFqName.isRoot() && (parentFqName.equals(JAVA_LANG) ||
        parentFqName.equals(currentPackage));

  }

  private static final class GenArrayImpl implements GenType {
    private final GenType elementType;

    private GenArrayImpl(@Nonnull GenType elementType) {
      this.elementType = elementType;
    }

    @Nonnull
    private GenType getElementType() {
      return elementType;
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
  }

  private static final class JavaClassRef implements GenClass {
    private final Class<?> originClass;
    private transient final FqName targetName;

    public JavaClassRef(@Nonnull Class<?> originClass) {
      this.originClass = originClass;
      this.targetName = FqName.parse(originClass.getName());
      assert !originClass.isArray();
    }

    public Class<?> getOriginClass() {
      return originClass;
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
  }
}
