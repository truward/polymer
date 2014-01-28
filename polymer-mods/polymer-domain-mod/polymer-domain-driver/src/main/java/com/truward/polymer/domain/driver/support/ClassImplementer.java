package com.truward.polymer.domain.driver.support;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.generator.model.LocalRefType;
import com.truward.polymer.core.generator.model.TypeVisitor;
import com.truward.polymer.core.naming.FqName;
import com.truward.polymer.domain.analysis.*;
import com.truward.polymer.domain.analysis.trait.GetterTrait;
import com.truward.polymer.domain.analysis.trait.SetterTrait;
import com.truward.polymer.domain.analysis.trait.SimpleDomainFieldTrait;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Encapsulates generation of the domain class' compilation unit
 *
 * @author Alexander Shabanov
 */
public final class ClassImplementer {
  // current generator
  private final JavaCodeGenerator generator;
  private final DomainAnalysisResult analysisResult;
  private final LocalRefType implClass;
  private final FqName targetName;
  private final DomainImplementerSettingsReader implementerSettings;

  public ClassImplementer(@Nonnull JavaCodeGenerator generator,
                          @Nonnull DomainImplementerSettingsReader implementerSettings,
                          @Nonnull DomainImplementationTarget target) {
    this.generator = generator;
    this.implementerSettings = implementerSettings;
    this.targetName = target.getTargetName();
    this.analysisResult = target.getAnalysisResult();
    this.implClass = new LocalRefType(targetName.getName());
  }

  @Nonnull
  public LocalRefType getTargetClass() {
    return implClass;
  }

  public void generateHeaderAndPrologue() {
    if (!targetName.isRoot()) {
      generator.packageDirective(targetName.getParent());
    }


    generator.textWithSpaces("public", "class").ch(' ').type(implClass);
    // implements
    generator.ch(' ').text("implements").ch(' ').type(analysisResult.getOriginClass());
    generator.ch(' ', '{');

    // fields
    for (final DomainField field : analysisResult.getFields()) {
      ImplementerUtil.generateField(generator, field, field.hasTrait(SimpleDomainFieldTrait.MUTABLE));
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
    generateToString(implClass, analysisResult.getFields());

    // hashCode
    generator.ch('\n');
    generateHashCode(analysisResult.getFields());

    // equals
    generator.ch('\n');
    generateEquals(implClass, analysisResult.getFields());
  }

  public void generateEpilogue() {
    generator.ch('}'); // end of class body
  }

  //
  // Private
  //

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
      generateNullCheckIfNeeded(field);
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

  //
  // equals/hashCode/toString
  //



  private void generateToString(Type implClass, Collection<? extends DomainField> fields) {
    generator.annotate(Override.class).text("public").ch(' ').type(String.class).ch(' ').text("toString").ch('(', ')', ' ', '{');

    // ==> final StringBuilder result = new StringBuilder();
    generator.text("final").ch(' ').type(StringBuilder.class).ch(' ').text("result").ch(' ', '=', ' ')
        .newType(StringBuilder.class).ch('(', ')', ';');

    // ==> result.append("ClassName#{");
    generator.text("result").dot("append").ch('(', '\"').type(implClass).text("#{").ch('\"', ')', ';');

    // fields
    boolean next = false;
    for (final DomainField field : fields) {
      generator.text("result");
      if (next) {
        generator.dot("append").ch('(', '\"').text(", ").ch('\"', ')');
      } else {
        next = true;
      }

      generator.dot("append").ch('(', '\"').text(field.getFieldName()).text(": ").ch('\"', ')');
      // TODO: another way to retrieve field - e.g. getter?
      generator.dot("append").ch('(').thisMember(field.getFieldName()).ch(')', ';');
    }

    // ==> result.append('}');
    generator.text("result").dot("append").ch('(', '\'').text("}").ch('\'', ')', ';');

    // ==> return result.toString();
    generator.text("return").ch(' ').text("result").dot("toString").ch('(', ')', ';');

    generator.ch('}'); // end of function
  }

  public void generateEquals(Type implClass, Collection<? extends DomainField> fields) {
    final String objectParam = "o";
    final String other = "other";

    generator.annotate(Override.class).text("public").ch(' ').type(boolean.class).ch(' ').text("equals").ch('(')
        .type(Object.class).ch(' ').text(objectParam).ch(')', ' ', '{');

    // if (this == o) return true;
    generator.text("if").ch(' ', '(').text("this").spText("==").text(objectParam).ch(')', ' ', '{')
        .text("return").ch(' ').text("true").ch(';').ch('}');

    // if (o == null || getClass() != o.getClass()) return false;
    generator.text("if").ch(' ', '(').text(objectParam).spText("==").text("null")
        .spText("||").text("getClass").ch('(', ')').spText("!=")
        .member(objectParam, "getClass").ch('(', ')')
        .ch(')', ' ', '{').text("return", "false").ch(';').ch('}').ch('\n');

    // final ClassName other = (ClassName) o;
    generator.text("final").ch(' ').type(implClass).ch(' ').text(other).ch(' ', '=', ' ', '(')
        .type(implClass).ch(')', ' ').text(objectParam).ch(';');

    // iterate over the given fields
    for (final DomainField field : fields) {
      // if (...) { return false; }
      generator.text("if").ch(' ', '(');

      // if-condition
      generateNonEqualsIfCondition(field, other);

      generator.ch(')', ' ', '{').text("return", "false").ch(';', '}');
    }

    // return true
    generator.ch('\n').text("return", "true").ch(';');

    generator.ch('}'); // end of function
  }

  private void generateHashCode(Collection<? extends DomainField> fields) {
    final String result = "result";
    final String temp = "temp";

    generator.annotate(Override.class).text("public").ch(' ').type(int.class).ch(' ').text("hashCode")
        .ch('(', ')', ' ', '{');

    // int result = 0
    generator.type(int.class).ch(' ').text(result).spText("=").ch('0', ';');
    // additional variable for calculating hash code for doubles
    boolean tempLongRequired = false;
    for (final DomainField field : fields) {
      final Class<?> fieldClass = TypeUtil.asClass(field);
      if (Double.TYPE.equals(fieldClass)) {
        tempLongRequired = true;
        break;
      }
    }

    if (tempLongRequired) {
      // long temp
      generator.type(long.class).ch(' ').text(temp).ch(';');
    }

    // result calculation
    for (final DomainField field : fields) {
      generateHashCodeAddition(field, result, temp);
    }

    // return result;
    generator.text("return", result).ch(';');

    generator.ch('}'); // end of function
  }

  private void generateNullCheckIfNeeded(DomainField field) {
    if (!TypeUtil.isNullCheckRequired(field)) {
      return;
    }

    // assuming param name equals to the field name
    final String paramName = field.getFieldName();
    generator.text("if").ch(' ').ch('(').text(paramName).spText("==").text("null").ch(')', ' ', '{');
    generator.text("throw").ch(' ').newType(IllegalArgumentException.class).ch('(', '\"')
        .text("Parameter " + paramName + " is null")
        .ch('\"', ')', ';');
    generator.ch('}');
  }

  private void generateHashCodeAddition(DomainField field, String result, String temp) {
    final Class<?> fieldClass = TypeUtil.asClass(field);
    final String fieldName = field.getFieldName();

    boolean doubleField = false;
    if (Double.TYPE.equals(fieldClass)) {
      // special case: double field
      doubleField = true;
      // temp = Double.doubleToLongBits(g);
      generator.text(temp).spText("=").type(Double.class).dot("doubleToLongBits").ch('(').thisMember(fieldName).ch(')', ';');
    }

    // Common code predecessor: result = 31 * result + ...;
    generator.text(result).spText("=").text("31").spText("*").text(result).spText("+");
    if (doubleField) {
      // (int) (temp ^ (temp >>> 32))
      generator.cast(int.class).ch('(').text(temp).spText("^")
          .ch('(').text(temp).spText(">>>").text("32").ch(')', ')');
    } else if (fieldClass != null && fieldClass.isPrimitive()) {
      // special cases for primitive types
      if (Boolean.TYPE.equals(fieldClass)) {
        // (this.field ? 1 : 0)
        generator.ch('(').thisMember(fieldName).spText("?").ch('1').spText(":").ch('0').ch(')');
      } else if (Byte.TYPE.equals(fieldClass) || Short.TYPE.equals(fieldClass) || Character.TYPE.equals(fieldClass)) {
        // for byte, short and char: (int) fieldClass
        generator.cast(int.class).thisMember(fieldName);
      } else if (Integer.TYPE.equals(fieldClass)) {
        // int type - use just member as is
        generator.thisMember(fieldName);
      } else if (Long.TYPE.equals(fieldClass)) {
        // (int) (e ^ (e >>> 32))
        generator.cast(int.class).ch('(').thisMember(fieldName).spText("^")
            .ch('(').thisMember(fieldName).spText(">>>").text("32").ch(')', ')');
      } else if (Float.TYPE.equals(fieldClass)) {
        // (f != +0.0f ? Float.floatToIntBits(f) : 0)
        generator.ch('(').thisMember(fieldName).spText("!=").text("+0.0f").spText("?")
            .type(Float.class).dot("floatToIntBits").ch('(').thisMember(fieldName).ch(')').spText(":").ch('0', ')');
      } else {
        throw new UnsupportedOperationException("Unsupported primitive type: " + fieldClass);
      }
    } else {
      // object case:
      if (TypeUtil.isNullCheckRequired(field)) {
        // ...=> (this.field != null ? this.field.hashCode() : null)
        generator.ch('(').thisMember(fieldName).spText("!=").text("null").spText("?")
            .thisMember(fieldName).dot("hashCode").ch('(', ')')
            .spText(":").text("null")
            .ch(')');
      } else {
        // ...=> this.field.hashCode()
        generator.thisMember(fieldName).dot("hashCode").ch('(', ')');
      }
    }

    generator.ch(';');
  }

  private void generateNonEqualsIfCondition(DomainField field, String other) {
    final String fieldName = field.getFieldName();
    final Class<?> fieldClass = TypeUtil.asClass(field);

    // special logic for primitive members
    if (fieldClass != null && fieldClass.isPrimitive()) {
      // float and double require special comparison
      if (fieldClass.equals(Float.class)) {
        // Float.compare(this.field, other.field) != 0
        generator.type(Float.class).dot("compare").ch('(')
            .thisMember(fieldName).ch(',', ' ').member(other, fieldName)
            .ch(')').spText("!=").ch('0');
        return;
      } else if (fieldClass.equals(Double.class)) {
        // Double.compare(this.field, other.field) != 0
        generator.type(Float.class).dot("compare").ch('(')
            .thisMember(fieldName).ch(',', ' ').member(other, fieldName)
            .ch(')').spText("!=").ch('0');
        return;
      }

      // this.field != other.field
      generator.thisMember(fieldName).spText("!=").member(other, fieldName);
      return;
    }

    // generic class case, use equals
    if (TypeUtil.isNullCheckRequired(field)) {
      // this.field != null ? !this.field.equals(other.field) : other.field != null
      generator.thisMember(fieldName).spText("!=").text("null").spText("?")
          .ch('!').thisMember(fieldName).dot("equals").ch('(').member(other, fieldName).ch(')')
          .spText(":").member(other, fieldName).spText("!=").text("null");
      return;
    }

    // !this.field.equals(other.equals)
    generator.ch('!').thisMember(fieldName).dot("equals").ch('(').member(other, fieldName).ch(')');
  }
}
