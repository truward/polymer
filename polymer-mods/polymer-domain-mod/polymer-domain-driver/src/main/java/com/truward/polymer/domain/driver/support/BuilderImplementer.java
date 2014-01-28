package com.truward.polymer.domain.driver.support;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.generator.model.LocalRefType;
import com.truward.polymer.core.generator.model.TypeVisitor;
import com.truward.polymer.core.types.DefaultParameterizedType;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.support.Names;
import com.truward.polymer.domain.analysis.trait.BuilderTrait;
import com.truward.polymer.domain.analysis.trait.GetterTrait;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Encapsulates builder generation.
 * TODO: non-inner builder support
 *
 * @author Alexander Shabanov
 */
public final class BuilderImplementer {
  private final JavaCodeGenerator generator;
  private final Type implClass;
  private final Type builderClass;
  private final DomainAnalysisResult analysisResult;

  public BuilderImplementer(@Nonnull JavaCodeGenerator generator,
                            @Nonnull Type implClass,
                            @Nonnull DomainAnalysisResult analysisResult) {
    this.generator = generator;
    this.implClass = implClass;
    this.analysisResult = analysisResult;

    // TODO: support non-inner builder types
    this.builderClass = new LocalRefType("Builder");
  }

  public boolean isInnerBuilderSupported() {
    return analysisResult.hasTrait(BuilderTrait.KEY);
  }

  public void generateInnerBuilder() {
    final List<DomainField> fields = analysisResult.getFields();

    generateEmptyNewBuilderMethod();
    generateNewBuilderMethod(fields);

    generator.ch('\n');

    // class Builder
    generator.ch('\n').textWithSpaces("public", "static", "final", "class").ch(' ').type(builderClass).ch(' ', '{');

    // builder fields
    for (final DomainField field : fields) {
      ImplementerUtil.generateField(generator, field, false);
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

    generateBuildMethod(fields);

    generator.ch('}'); // end of 'class Builder'
  }

  private void generateEmptyNewBuilderMethod() {
    // newBuilder() method
    generator.ch('\n').text("public").ch(' ').text("static").ch(' ').type(builderClass).ch(' ').text("newBuilder")
        .ch('(', ')', ' ', '{')
        .text("return").ch(' ').newType(builderClass).ch('(', ')', ';')
        .ch('}');
  }

  private void generateNewBuilderMethod(List<DomainField> fields) {
    // newBuilder({TargetClass} value) method
    final String valueParam = Names.VALUE;
    final String resultVar = Names.RESULT;
    generator.ch('\n').text("public").ch(' ').text("static").ch(' ').type(builderClass).ch(' ').text("newBuilder")
        .ch('(').type(analysisResult.getOriginClass()).ch(' ').text(valueParam).ch(')', ' ', '{')
        .text("final").ch(' ').type(builderClass).ch(' ').text(resultVar).ch(' ', '=', ' ')
        .text("newBuilder").ch('(', ')', ';');

    // explodes into invocation of multiple setters in the current builder
    for (final DomainField field : fields) {
      final String fieldName = field.getFieldName();
      generator.text(resultVar);
      TypeVisitor.apply(new TypeVisitor<Void>() {
        @Override
        public Void visitType(@Nonnull Type sourceType) {
          generator.dot(Names.createPrefixedName(Names.SET_PREFIX, fieldName));
          return null;
        }

        @Override
        public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
          if (List.class.equals(rawType) || Set.class.equals(rawType)) {
            generator.dot(Names.createPrefixedName("addAllTo", fieldName));
            return null;
          } else if (Map.class.equals(rawType)) {
            generator.dot(Names.createPrefixedName("putAllTo", fieldName));
            return null;
          }

          return visitType(sourceType);
        }
      }, field.getFieldType());

      // TODO: make generation of this method entirely optional
      final GetterTrait getterTrait = field.findTrait(GetterTrait.KEY);
      if (getterTrait == null) {
        throw new UnsupportedOperationException("Can't generate newBuilder for field that has no getters in the " +
            "origin interface: " + field.getClass() + ": " + field.getFieldName());
      }
      generator.ch('(').text(valueParam).dot(getterTrait.getGetterName()).ch('(', ')', ')', ';');
    }

    generator.text("return").ch(' ').text(resultVar).ch(';')
        .ch('}');
  }

  private void generateBuildMethod(List<DomainField> fields) {
    generator.ch('\n').text("public").ch(' ').type(analysisResult.getOriginClass()).ch(' ').text("build")
        .ch('(', ')', ' ', '{').text("return").ch(' ').newType(implClass).ch('(');
    boolean next = false;
    for (final DomainField field : fields) {
      if (next) {
        generator.ch(',', ' ');
      } else {
        next = true;
      }
      generator.text(field.getFieldName());
    }
    generator.ch(')', ';', '}');
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
}
