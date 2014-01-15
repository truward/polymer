package com.truward.polymer.domain.synthesis.support;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.generator.model.LocalRefType;
import com.truward.polymer.core.generator.model.TypeVisitor;
import com.truward.polymer.core.generator.support.DefaultParameterizedType;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.support.Names;
import com.truward.polymer.domain.analysis.trait.BuilderTrait;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Alexander Shabanov
 */
public final class BuilderImplementer {
  // current generator
  private final JavaCodeGenerator generator;

  public BuilderImplementer(@Nonnull JavaCodeGenerator generator) {
    this.generator = generator;
  }

  public void generateInnerBuilder(@Nonnull Type implClass, @Nonnull DomainAnalysisResult analysisResult) {
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
}
