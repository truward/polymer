package com.truward.polymer.domain.implementer;

import com.truward.polymer.core.code.builder.CodeStream;
import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.TypeVisitor;
import com.truward.polymer.core.types.SynteticParameterizedType;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.FieldUtil;
import com.truward.polymer.domain.analysis.OriginMethodRole;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.domain.analysis.support.Names;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Encapsulates builder generation.
 * TODO: non-inner builder support
 *
 * @author Alexander Shabanov
 */
public final class BuilderImplementer extends AbstractDomainImplementer {

  public BuilderImplementer(@Nonnull CodeStream codeStream, @Nonnull GenDomainClass domainClass) {
    super(codeStream, domainClass);
    if (!domainClass.getGenBuilderClass().isSupported()) {
      throw new IllegalStateException("Builder class is not supported");
    }
    if (!domainClass.getGenBuilderClass().hasFqName()) {
      throw new IllegalStateException("Builder class has no name");
    }
  }

  @Nonnull
  public GenClass getBuilderClass() {
    return getDomainClass().getGenBuilderClass();
  }

  public void generateInnerBuilder() {
    final List<DomainField> fields = getAnalysisResult().getFields();

    generateEmptyNewBuilderMethod();
    generateNewBuilderMethod(fields);

    c('\n');

    // class Builder
    c('\n').s("public").sps("static").s("final").sps("class")
        .t(getBuilderClass()).sp().c('{');

    // builder fields
    for (final DomainField field : fields) {
      field(field, Modifier.PRIVATE);
    }

    // private constructor
    eol().s("private").sp().t(getBuilderClass()).c('(').c(')', ' ', '{');
    // we need to initialize certain fields
    for (final DomainField field : fields) {
      generateInitializerForBuilderField(field);
    }
    c('}');

    // setters
    for (final DomainField field : fields) {
      generateSettersForBuilder(field);
    }

    generateBuildMethod(fields);

    c('}'); // end of 'class Builder'
  }

  private void generateEmptyNewBuilderMethod() {
    // newBuilder() method
    c('\n').s("public").sps("static").t(getBuilderClass()).sp().s("newBuilder")
        .c('(', ')', ' ', '{')
        .s("return").sp().newType(getBuilderClass()).c('(', ')', ';')
        .c('}');
  }

  private void generateNewBuilderMethod(List<DomainField> fields) {
    // newBuilder({TargetClass} value) method
    final String valueParam = Names.VALUE;
    final String resultVar = Names.RESULT;
    c('\n').s("public").sp().s("static").sp().t(getBuilderClass()).sp().s("newBuilder")
        .c('(').t(getOriginClass()).sp().s(valueParam).c(')', ' ', '{')
        .s("final").sp().t(getBuilderClass()).sp().s(resultVar).c(' ', '=', ' ')
        .s("newBuilder").c('(', ')', ';');

    // explodes into invocation of multiple setters in the current builder
    for (final DomainField field : fields) {
      final String fieldName = field.getFieldName();
      s(resultVar);
      TypeVisitor.apply(new TypeVisitor<Void>() {
        @Override
        public Void visitType(@Nonnull Type sourceType) {
          dot(Names.createPrefixedName(Names.SET_PREFIX, fieldName));
          return null;
        }

        @Override
        public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
          if (List.class.equals(rawType) || Set.class.equals(rawType)) {
            dot(Names.createPrefixedName("addAllTo", fieldName));
            return null;
          } else if (Map.class.equals(rawType)) {
            dot(Names.createPrefixedName("putAllTo", fieldName));
            return null;
          }

          return visitType(sourceType);
        }
      }, field.getFieldType());

      // TODO: make generation of this method entirely optional

      final String getterName = FieldUtil.getMethodName(field, OriginMethodRole.GETTER);
      if (getterName == null) {
        throw new UnsupportedOperationException("Can't generate newBuilder for field that has no getters in the " +
            "origin interface: " + field.getClass() + ": " + field.getFieldName());
      }
      c('(').s(valueParam).dot(getterName).c('(', ')', ')', ';');
    }

    s("return").sp().s(resultVar).c(';')
        .c('}');
  }

  private void generateBuildMethod(List<DomainField> fields) {
    c('\n').s("public").sp().t(getOriginClass()).sp().s("build")
        .c('(', ')', ' ', '{').s("return").sp().newType(getDomainClass()).c('(');
    boolean next = false;
    for (final DomainField field : fields) {
      if (next) {
        c(',', ' ');
      } else {
        next = true;
      }
      s(field.getFieldName());
    }
    c(')', ';', '}');
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
          thisDot(fieldName).c(' ', '=', ' ')
              .newType(SynteticParameterizedType.from(rawTypeForCopy, args)).c('(').s(fieldName).c(')', ';');
          return null;
        }

        return null;
      }
    }, field.getFieldType());
  }

  private void generateSettersForBuilder(DomainField field) {
    final String fieldName = field.getFieldName();
    TypeVisitor.apply(new TypeVisitor<Void>() {
      @Override
      public Void visitType(@Nonnull Type sourceType) {
        generateBuilderSetter(getDomainClass(), sourceType, fieldName);
        return null;
      }

      @Override
      public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
        // Special case for lists, sets and maps
        if (List.class.equals(rawType)) {
          assert args.size() == 1;
          // Special setters: add & addAll
          generateBuilderAppender(getDomainClass(), args.get(0), fieldName);
          generateBuilderBulkAppender(getDomainClass(), sourceType, fieldName);
          return null;
        } else if (Map.class.equals(rawType)) {
          assert args.size() == 2;
          final Type keyType = args.get(0);
          final Type valueType = args.get(1);
          generateBuilderPut(getDomainClass(), keyType, valueType, fieldName);
          generateBuilderPutAll(getDomainClass(), sourceType, fieldName);
          return null;
        } else if (Set.class.equals(rawType)) {
          assert args.size() == 1;
          // Special setters: add & addAll
          generateBuilderAppender(getDomainClass(), args.get(0), fieldName);
          generateBuilderBulkAppender(getDomainClass(), sourceType, fieldName);
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

    c('\n')
        .s("public").sp().t(builderClass).sp()
        .s(Names.createPrefixedName(Names.SET_PREFIX, fieldName)).c('(');
    // arg - ({FieldType} {fieldName})
    var(fieldType, fieldName);
    c(')', ' ', '{');
    // this.{fieldName} = {fieldName};
    thisDot(fieldName).spc('=').s(fieldName).c(';');
    // return this;
    s("return").sp().s("this").c(';');
    c('}');
  }

  private void generateBuilderAppender(Type builderClass, Type elementType, String fieldName) {
    final String elementParam = Names.ELEMENT;

    c('\n')
        .s("public").sp().t(builderClass).sp()
        .s(Names.createPrefixedName("addTo", fieldName)).c('(');
    // arg - ({ElementType} element)
    var(elementType, elementParam);
    c(')', ' ', '{');
    // this.{fieldName}.add(element);
    thisDot(fieldName).dot("add").c('(').s(elementParam).c(')', ';');
    // return this;
    s("return").sp().s("this").c(';');
    c('}');
  }

  private void generateBuilderBulkAppender(Type builderClass, Type fieldType, String fieldName) {
    final String paramName = Names.ELEMENTS;

    c('\n')
        .s("public").sp().t(builderClass).sp()
        .s(Names.createPrefixedName("addAllTo", fieldName)).c('(');
    // arg - ({FieldType} elements)
    var(fieldType, paramName);
    c(')', ' ', '{');
    // this.{fieldName}.addAll(elements);
    thisDot(fieldName).dot("addAll").c('(').s(paramName).c(')', ';');
    // return this;
    s("return").sp().s("this").c(';');
    c('}');
  }

  private void generateBuilderPut(Type builderClass, Type keyType, Type valueType, String fieldName) {
    final String keyParam = Names.KEY;
    final String valueParam = Names.VALUE;

    c('\n')
        .s("public").sp().t(builderClass).sp()
        .s(Names.createPrefixedName("putTo", fieldName)).c('(');
    // arg - ({KeyType} key, {ValueType} value)
    var(keyType, keyParam).c(',', ' ').var(valueType, valueParam);
    c(')', ' ', '{');
    // this.{fieldName}.put(key, value);
    thisDot(fieldName).dot("put").c('(').s(keyParam).c(',', ' ').s(valueParam).c(')', ';');
    // return this;
    s("return").sp().s("this").c(';');
    c('}');
  }

  private void generateBuilderPutAll(Type builderClass, Type fieldType, String fieldName) {
    final String elementsName = Names.ELEMENTS;

    c('\n')
        .s("public").sp().t(builderClass).sp()
        .s(Names.createPrefixedName("putAllTo", fieldName)).c('(');
    // arg - ({FieldType} elements)
    var(fieldType, elementsName);
    c(')', ' ', '{');
    // this.{fieldName}.addAll(elements);
    thisDot(fieldName).dot("putAll").c('(').s(elementsName).c(')', ';');
    // return this;
    s("return").sp().s("this").c(';');
    c('}');
  }
}
