package com.truward.polymer.domain.synthesis;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.core.generator.model.LocalRefType;
import com.truward.polymer.core.generator.model.TypeVisitor;
import com.truward.polymer.code.naming.FqName;
import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.generator.support.DefaultParameterizedType;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;
import com.truward.polymer.domain.analysis.support.Names;
import com.truward.polymer.domain.analysis.trait.BuilderTrait;
import com.truward.polymer.domain.analysis.trait.GetterTrait;
import com.truward.polymer.domain.analysis.trait.SetterTrait;
import com.truward.polymer.domain.analysis.trait.SimpleDomainFieldTrait;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Generates a code that corresponds to the particular implementation targets
 *
 * @author Alexander Shabanov
 */
public final class DomainObjectImplementer {

  // current generator
  private JavaCodeGenerator generator;

  @Resource
  private DomainImplementerSettingsReader implementerSettings;

  public void setImplementerSettings(DomainImplementerSettingsReader implementerSettingsReader) {
    this.implementerSettings = implementerSettingsReader;
  }

  public void generateCode(@Nonnull OutputStreamProvider outputStreamProvider,
                           @Nonnull List<DomainAnalysisResult> implTargets) {
    for (final DomainAnalysisResult target : implTargets) {
      this.generator = new JavaCodeGenerator();
      final FqName classFqName = getImplClassName(target);

      generateCompilationUnit(target, classFqName);
      try {
        try (final OutputStream stream = outputStreamProvider.createStreamForFile(classFqName, DefaultFileTypes.JAVA)) {
          try (final PrintStream printStream = new PrintStream(stream, true, StandardCharsets.UTF_8.name())) {
            generator.printContents(printStream);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private FqName getImplClassName(@Nonnull DomainAnalysisResult result) {
    final StringBuilder nameBuilder = new StringBuilder(200);
    nameBuilder.append(implementerSettings.getDefaultTargetPackageName()).append('.');
    nameBuilder.append(implementerSettings.getDefaultImplClassPrefix())
        .append(result.getOriginClass().getSimpleName())
        .append(implementerSettings.getDefaultImplClassSuffix());

    return FqName.parse(nameBuilder.toString());
  }

  private void generateCompilationUnit(DomainAnalysisResult analysisResult, FqName classFqName) {
    final LocalRefType implClass = new LocalRefType(classFqName.getName());

    if (!classFqName.isRoot()) {
      generator.packageDirective(classFqName.getParent());
    }


    generator.textWithSpaces("public", "class").ch(' ').type(implClass);
    // implements
    generator.ch(' ').text("implements").ch(' ').type(analysisResult.getOriginClass());
    generator.ch(' ', '{');

    // fields
    for (final DomainField field : analysisResult.getFields()) {
      generateField(field, field.hasTrait(SimpleDomainFieldTrait.MUTABLE));
    }

    // ctor
    generateConstructor(analysisResult, implClass);

    // getters
    for (final DomainField field : analysisResult.getFields()) {
      generateFinalGetter(field);
    }

    // setters
    for (final DomainField field : analysisResult.getFields()) {
      generateFinalSetter(field);
    }

    // toString
    generator.ch('\n');
    ImplementerUtil.generateToString(generator, implClass, analysisResult.getFields());

    // hashCode
    generator.ch('\n');
    ImplementerUtil.generateHashCode(generator, analysisResult.getFields());

    // equals
    generator.ch('\n');
    ImplementerUtil.generateEquals(generator, implClass, analysisResult.getFields());

    // inner builder class and corresponding methods
    generateInnerBuilder(implClass, analysisResult);

    generator.ch('}'); // end of class body
  }

  private void generateInnerBuilder(Type implClass, DomainAnalysisResult analysisResult) {
    final BuilderTrait builderTrait = analysisResult.findTrait(BuilderTrait.KEY);
    if (builderTrait == null) {
      return; // no builder expected
    }

    final LocalRefType builderClass = new LocalRefType("Builder");

    // newBuilder() method
    generator.ch('\n').text("public").ch(' ').text("static").ch(' ').type(implClass).ch(' ').text("newBuilder")
        .ch('(', ')', ' ', '{')
        .text("return").ch(' ').newType(builderClass).ch('(', ')', ';')
        .ch('}');

    generator.ch('\n');

    // class Builder
    generator.ch('\n').textWithSpaces("public", "static", "final", "class").ch(' ').type(builderClass).ch('{');

    final List<DomainField> fields = analysisResult.getFields();

    // builder fields
    for (final DomainField field : fields) {
      generateField(field, false);
    }

    // private constructor
    generator.ch('\n').text("private").ch(' ').type(builderClass).ch('(').ch(')', ' ', '{');
    // we need to initialize certain fields
    for (final DomainField field : fields) {
      generateInitializerForBuilderField(field);
    }
    generator.ch('}');

    // setters
    for (final DomainField field : fields) {
      generateSettersForBuilder(field, builderClass);
    }


    generator.ch('}'); // end of 'class Builder'
  }

  private void generateInitializerForBuilderField(DomainField field) {
    final String fieldName = field.getFieldName();
    TypeVisitor.apply(new TypeVisitor<Void>() {
      @Override
      public Void visitType(@Nonnull Type sourceType) {
        return null; // no special initialization is required
      }

      @Override
      public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
        // Special case for lists, sets and maps
        Class<?> rawTypeForCopy = null;
        if (List.class.equals(rawType)) {
          // TODO: LinkedList, CopyOnWriteArrayList, etc.?
          rawTypeForCopy = ArrayList.class;
        } else if (Map.class.equals(rawType)) {
          // TODO: LinkedHashMap, TreeMap, ConcurrentHashMap, etc.?
          rawTypeForCopy = HashMap.class;
        } else if (Set.class.equals(rawType)) {
          // TODO: LinkedHashSet, HashSet, CopyOnWriteArraySet, etc.?
          rawTypeForCopy = HashSet.class;
        }

        // generate copy: new RawType<{Args..}>(fieldName); - e.g. new ArrayList<{Type}(fieldName);
        if (rawTypeForCopy != null) {
          generator.thisMember(fieldName).ch(' ', '=', ' ')
              .newType(DefaultParameterizedType.from(rawTypeForCopy, args)).ch('(').text(fieldName).ch(')', ';');
          return null;
        }

        return null;
      }
    }, field.getFieldType());
  }

  private void generateSettersForBuilder(DomainField field, final Type builderClass) {
    final String fieldName = field.getFieldName();
    TypeVisitor.apply(new TypeVisitor<Void>() {
      @Override
      public Void visitType(@Nonnull Type sourceType) {
        generateBuilderSetter(builderClass, sourceType, fieldName);
        return null;
      }

      @Override
      public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
        // Special case for lists, sets and maps
        if (List.class.equals(rawType)) {
          assert args.size() == 1;
          // Special setters: add & addAll
          generateBuilderAppender(builderClass, args.get(0), fieldName);
          generateBuilderBulkAppender(builderClass, sourceType, fieldName);
          return null;
        } else if (Map.class.equals(rawType)) {
          assert args.size() == 2;
          final Type keyType = args.get(0);
          final Type valueType = args.get(1);
          generateBuilderPut(builderClass, keyType, valueType, fieldName);
          generateBuilderPutAll(builderClass, sourceType, fieldName);
          return null;
        } else if (Set.class.equals(rawType)) {
          assert args.size() == 1;
          // Special setters: add & addAll
          generateBuilderAppender(builderClass, args.get(0), fieldName);
          generateBuilderBulkAppender(builderClass, sourceType, fieldName);
          return null;
        }

        return visitType(sourceType);
      }
    }, field.getFieldType());
  }

  private void generateBuilderSetter(Type builderClass, Type fieldType, String fieldName) {
    // public Builder set{FieldName}({FieldType} {fieldName}) {
    //    this.{fieldName} = {fieldName};
    //    return this;
    // }

    generator.ch('\n')
        .text("public").ch(' ').type(builderClass).ch(' ')
        .text(Names.createPrefixedName(Names.SET_PREFIX, fieldName)).ch('(');
    // arg - ({FieldType} {fieldName})
    generator.typedVar(fieldType, fieldName);
    generator.ch(')', ' ', '{');
    // this.{fieldName} = {fieldName};
    generator.thisMember(fieldName).ch(' ').text("=").ch(' ').text(fieldName).ch(';');
    // return this;
    generator.text("return").ch(' ').text("this").ch(';');
    generator.ch('}');
  }

  private void generateBuilderAppender(Type builderClass, Type elementType, String fieldName) {
    final String elementParam = Names.ELEMENT;

    generator.ch('\n')
        .text("public").ch(' ').type(builderClass).ch(' ')
        .text(Names.createPrefixedName("addTo", fieldName)).ch('(');
    // arg - ({ElementType} element)
    generator.typedVar(elementType, elementParam);
    generator.ch(')', ' ', '{');
    // this.{fieldName}.add(element);
    generator.thisMember(fieldName).dot("add").ch('(').text(elementParam).ch(')', ';');
    // return this;
    generator.text("return").ch(' ').text("this").ch(';');
    generator.ch('}');
  }

  private void generateBuilderBulkAppender(Type builderClass, Type fieldType, String fieldName) {
    final String paramName = Names.ELEMENTS;

    generator.ch('\n')
        .text("public").ch(' ').type(builderClass).ch(' ')
        .text(Names.createPrefixedName("addAllTo", fieldName)).ch('(');
    // arg - ({FieldType} elements)
    generator.typedVar(fieldType, paramName);
    generator.ch(')', ' ', '{');
    // this.{fieldName}.addAll(elements);
    generator.thisMember(fieldName).dot("addAll").ch('(').text(paramName).ch(')', ';');
    // return this;
    generator.text("return").ch(' ').text("this").ch(';');
    generator.ch('}');
  }

  private void generateBuilderPut(Type builderClass, Type keyType, Type valueType, String fieldName) {
    final String keyParam = Names.KEY;
    final String valueParam = Names.VALUE;

    generator.ch('\n')
        .text("public").ch(' ').type(builderClass).ch(' ')
        .text(Names.createPrefixedName("putTo", fieldName)).ch('(');
    // arg - ({KeyType} key, {ValueType} value)
    generator.typedVar(keyType, keyParam).ch(',', ' ').typedVar(valueType, valueParam);
    generator.ch(')', ' ', '{');
    // this.{fieldName}.put(key, value);
    generator.thisMember(fieldName).dot("put").ch('(').text(keyParam).ch(',', ' ').text(valueParam).ch(')', ';');
    // return this;
    generator.text("return").ch(' ').text("this").ch(';');
    generator.ch('}');
  }

  private void generateBuilderPutAll(Type builderClass, Type fieldType, String fieldName) {
    final String elementsName = Names.ELEMENTS;

    generator.ch('\n')
        .text("public").ch(' ').type(builderClass).ch(' ')
        .text(Names.createPrefixedName("putAllTo", fieldName)).ch('(');
    // arg - ({FieldType} elements)
    generator.typedVar(fieldType, elementsName);
    generator.ch(')', ' ', '{');
    // this.{fieldName}.addAll(elements);
    generator.thisMember(fieldName).dot("putAll").ch('(').text(elementsName).ch(')', ';');
    // return this;
    generator.text("return").ch(' ').text("this").ch(';');
    generator.ch('}');
  }

  private void generateFinalSetter(DomainField field) {
    final SetterTrait setterTrait = field.findTrait(SetterTrait.KEY);
    if (setterTrait == null) {
      // no setter
      return;
    }

    final String fieldName = field.getFieldName();

    // @Override public void set{FieldName}
    generator.ch('\n')
        .annotate(Override.class).text("public").ch(' ').text("final").ch(' ').type(void.class).ch(' ')
        .text(setterTrait.getSetterName()).ch('(');
    // arg - ({FieldType} {FieldName})
    generator.typedVar(field.getFieldType(), fieldName);
    generator.ch(')', ' ', '{');
    // impl { this.{FieldName} = {FieldName}; }
    generator.thisMember(fieldName).ch(' ').text("=").ch(' ').text(fieldName).ch(';');
    generator.ch('}');
  }

  //
  // Private
  //

  private void generateField(DomainField field, boolean isFinal) {
    generator.text("private").ch(' ');
    if (!isFinal) {
      generator.text("final").ch(' ');
    }
    generator.typedVar(field.getFieldType(), field.getFieldName()).ch(';');
  }

  private void generateFinalGetter(DomainField field) {
    final GetterTrait getterTrait = field.findTrait(GetterTrait.KEY);
    if (getterTrait == null) {
      return; // no getter trait
    }

    generator.ch('\n')
        .annotate(Override.class).text("public").ch(' ').text("final").ch(' ').type(field.getFieldType()).ch(' ')
        .text(getterTrait.getGetterName()).ch('(', ')', ' ', '{');
    generator.text("return").ch(' ').thisMember(field.getFieldName()).ch(';');
    generator.ch('}');
  }

  private void generateConstructor(DomainAnalysisResult analysisResult, Type implClass) {
    generator.ch('\n');
    generator.text("public").ch(' ').type(implClass).ch('(');
    boolean next = false;
    for (final DomainField field : analysisResult.getFields()) {
      if (next) {
        generator.ch(',', ' ');
      } else {
        next = true;
      }

      // type
      generator.type(field.getFieldType());

      // space and name
      generator.ch(' ').text(field.getFieldName());
    }
    generator.ch(')', ' ', '{');

    // verification of the input arguments
    for (final DomainField field : analysisResult.getFields()) {
      ImplementerUtil.generateNullCheckIfNeeded(generator, field);
    }

    // body
    for (final DomainField field : analysisResult.getFields()) {
      generateAssignment(field);
    }
    generator.ch('}');
  }

  private void generateAssignment(DomainField field) {
    final JavaCodeGenerator generator = this.generator;
    final String fieldName = field.getFieldName();
    generator.thisMember(fieldName).spText("=");
    generateCopy(fieldName, field.getFieldType());
    generator.ch(';');
  }

  private void generateCopy(final String var, Type type) {
    TypeVisitor.apply(new TypeVisitor<Void>() {
      @Override
      public Void visitType(@Nonnull Type sourceType) {
        generator.text(var);
        return null;
      }

      @Override
      public Void visitArray(@Nonnull Type sourceType, @Nonnull Class<?> elementType) {
        // TODO: warning - exposed arrays breaks immutability
        // TODO: Array.copy(fieldName);
        return visitType(sourceType);
      }

      @Override
      public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
        switch (implementerSettings.getDefensiveCopyStyle()) {
          case JDK:
            if (List.class.equals(rawType)) {
              assert args.size() == 1;
              generateListCopyJdk(var, args.get(0));
            } else if (Map.class.equals(rawType)) {
              assert args.size() == 2;
              generateMapCopyJdk(var, args.get(0), args.get(1));
            } else if (Set.class.equals(rawType)) {
              assert args.size() == 1;
              generateSetCopyJdk(var, args.get(0));
            } else {
              break;
            }
            return null;

          case GUAVA:
            if (List.class.equals(rawType)) {
              generateListCopyGuava(var);
            } else if (Map.class.equals(rawType)) {
              generateMapCopyGuava(var);
            } else if (Set.class.equals(rawType)) {
              generateSetCopyGuava(var);
            } else {
              break;
            }
            return null;

          case NONE:
            break;

          default:
            throw new UnsupportedOperationException("Unsupported defensive copy style");
        }

        return visitType(sourceType);
      }

      @Override
      public Void visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
        return visitType(sourceType);
      }
    }, type);
  }

  //
  // JDK-style defensive copies
  //

  private void generateListCopyJdk(String var, Type elementType) {
    // Collections.unmodifiableList(Arrays.asList($listVar.toArray(new String[$listVar.size()])))
    generator.type(Collections.class).dot("unmodifiableList").ch('(')
        .type(Arrays.class).dot("asList").ch('(').text(var).dot("toArray").ch('(')
        .newType(elementType).ch('[').text(var).dot("size").ch('(', ')', ']')
        .ch(')').ch(')').ch(')');
  }

  private void generateMapCopyJdk(String var, Type keyType, Type valueType) {
    // Collections.unmodifiableMap(new HashMap<{KeyType}, {ValueType}>(var))
    generator.type(Collections.class).dot("unmodifiableMap").ch('(')
        .newType(HashMap.class).ch('<').type(keyType).ch(',', ' ').type(valueType).ch('>').ch('(').text(var).ch(')')
        .ch(')');
  }

  private void generateSetCopyJdk(String var, Type elementType) {
    // Collections.unmodifiableSet(new HashSet<{ElementType}>(var))
    generator.type(Collections.class).dot("unmodifiableSet").ch('(')
        .newType(HashSet.class).ch('<').type(elementType).ch('>').ch('(').text(var).ch(')')
        .ch(')');
  }

  //
  // Guava-style defensive copies
  //

  private void generateListCopyGuava(String var) {
    generator.type(ImmutableList.class).dot("copyOf").ch('(').text(var).ch(')');
  }

  private void generateMapCopyGuava(String var) {
    generator.type(ImmutableMap.class).dot("copyOf").ch('(').text(var).ch(')');
  }

  private void generateSetCopyGuava(String var) {
    generator.type(ImmutableSet.class).dot("copyOf").ch('(').text(var).ch(')');
  }
}
