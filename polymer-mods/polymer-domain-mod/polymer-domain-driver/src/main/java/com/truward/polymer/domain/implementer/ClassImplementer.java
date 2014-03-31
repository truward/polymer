package com.truward.polymer.domain.implementer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.core.code.builder.CodeStream;
import com.truward.polymer.core.code.typed.TypeVisitor;
import com.truward.polymer.core.types.DefaultValues;
import com.truward.polymer.domain.analysis.*;
import com.truward.polymer.domain.analysis.support.GenDomainClass;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Encapsulates generation of the domain class' compilation unit
 *
 * @author Alexander Shabanov
 */
public final class ClassImplementer extends AbstractDomainImplementer {
  // current generator
  private final DomainImplementerSettingsReader implementerSettings;

  public ClassImplementer(@Nonnull CodeStream codeStream, @Nonnull GenDomainClass domainClass,
                          @Nonnull DomainImplementerSettingsReader implementerSettings) {
    super(codeStream, domainClass);
    this.implementerSettings = implementerSettings;
  }

  public void generateHead() {
    publicFinalClass().s(getDomainClass().getFqName().getName());

    // implements
    sp().s("implements").sp().t(getAnalysisResult().getOriginClass()).c(' ', '{');

    // fields
    for (final DomainField field : getAnalysisResult().getFields()) {
      final List<Modifier> modifiers = field.hasTrait(FieldTrait.MUTABLE) ? ImmutableList.<Modifier>of() :
          ImmutableList.of(Modifier.FINAL);
      field(field, modifiers);
    }

    // ctor
    generateConstructor();

    // getters
    for (final DomainField field : getAnalysisResult().getFields()) {
      generateFinalGetter(field);
    }

    // setters
    for (final DomainField field : getAnalysisResult().getFields()) {
      generateFinalSetter(field);
    }

    // toString
    c('\n');
    generateToString(getAnalysisResult().getFields());

    // hashCode
    c('\n');
    generateHashCode(getAnalysisResult().getFields());

    // equals
    c('\n');
    generateEquals(getAnalysisResult().getFields());
  }

  public void generateEpilogue() {
    c('}'); // end of class body
  }

  //
  // Private
  //

  private void generateFinalSetter(DomainField field) {
    final String setterName = FieldUtil.getMethodName(field, OriginMethodRole.SETTER);
    if (setterName == null) {
      // no setter
      return;
    }

    final String fieldName = field.getFieldName();

    // @Override public void set{FieldName}
    c('\n');
    annotate(Override.class).s("public").sp().s("final").sp().t(void.class).sp();
    s(setterName).c('(');
    // arg - ({FieldType} {FieldName})
    var(field.getFieldType(), fieldName);
    c(')', ' ', '{');
    // impl { this.{FieldName} = {FieldName}; }
    thisDot(fieldName).c(' ', '=', ' ').s(fieldName).c(';');
    c('}');
  }


  private void generateFinalGetter(DomainField field) {
    final String getterName = FieldUtil.getMethodName(field, OriginMethodRole.GETTER);
    if (getterName == null) {
      return; // no getter trait
    }

    c('\n');
    annotate(Override.class).s("public").sp().s("final").sp().t(field.getFieldType()).sp();
    s(getterName).c('(', ')', ' ', '{');
    s("return").sp().thisDot(field.getFieldName()).c(';');
    c('}');
  }

  private void generateConstructor() {
    c('\n');
    s("public").sp().t(getDomainClass()).c('(');
    boolean next = false;
    for (final DomainField field : getAnalysisResult().getFields()) {
      if (next) {
        c(',', ' ');
      } else {
        next = true;
      }

      // typed
      t(field.getFieldType());

      // space and name
      c(' ').s(field.getFieldName());
    }
    c(')', ' ', '{');

    // verification of the input arguments
    for (final DomainField field : getAnalysisResult().getFields()) {
      generateChecks(field);
    }

    // body
    for (final DomainField field : getAnalysisResult().getFields()) {
      generateAssignment(field);
    }
    c('}');
  }

  private void generateAssignment(DomainField field) {
    final String fieldName = field.getFieldName();
    thisDot(fieldName).spc('=');
    generateCopy(fieldName, field.getFieldType());
    c(';');
  }

  private void generateCopy(final String var, Type type) {
    TypeVisitor.apply(new TypeVisitor<Void>() {
      @Override
      public Void visitType(@Nonnull Type sourceType) {
        s(var);
        return null;
      }

      @Override
      public Void visitArray(@Nonnull Type sourceType, @Nonnull Type elementType) {
        // TODO: warning - exposed arrays breaks immutability
        // TODO: Array.copy(fieldName);
        return visitType(sourceType);
      }

      @Override
      public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Type rawType, @Nonnull List<? extends Type> args) {
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
    t(Collections.class).dot("unmodifiableList").c('(')
        .t(Arrays.class).dot("asList").c('(').s(var).dot("toArray").c('(')
        .newType(elementType).c('[').s(var).dot("size").c('(', ')', ']')
        .c(')').c(')').c(')');
  }

  private void generateMapCopyJdk(String var, Type keyType, Type valueType) {
    // Collections.unmodifiableMap(new HashMap<{KeyType}, {ValueType}>(var))
    t(Collections.class).dot("unmodifiableMap").c('(')
        .newType(HashMap.class).c('<').t(keyType).c(',', ' ').t(valueType).c('>').c('(').s(var).c(')')
        .c(')');
  }

  private void generateSetCopyJdk(String var, Type elementType) {
    // Collections.unmodifiableSet(new HashSet<{ElementType}>(var))
    t(Collections.class).dot("unmodifiableSet").c('(')
        .newType(HashSet.class).c('<').t(elementType).c('>').c('(').s(var).c(')')
        .c(')');
  }

  //
  // Guava-style defensive copies
  //

  private void generateListCopyGuava(String var) {
    t(ImmutableList.class).dot("copyOf").c('(').s(var).c(')');
  }

  private void generateMapCopyGuava(String var) {
    t(ImmutableMap.class).dot("copyOf").c('(').s(var).c(')');
  }

  private void generateSetCopyGuava(String var) {
    t(ImmutableSet.class).dot("copyOf").c('(').s(var).c(')');
  }

  //
  // equals/hashCode/toString
  //



  private void generateToString(Collection<? extends DomainField> fields) {
    annotate(Override.class).s("public").sp().t(String.class).sp().s("toString").c('(', ')', ' ', '{');

    // ==> final StringBuilder result = new StringBuilder();
    s("final").sp().t(StringBuilder.class).sp().s("result").c(' ', '=', ' ')
        .newType(StringBuilder.class).c('(', ')', ';');

    // ==> result.append("ClassName#{");
    s("result").dot("append").c('(', '\"').t(getDomainClass()).s("#{").c('\"', ')', ';');

    // fields
    boolean next = false;
    for (final DomainField field : fields) {
      s("result");
      if (next) {
        dot("append").c('(', '\"').s(", ").c('\"', ')');
      } else {
        next = true;
      }

      dot("append").c('(', '\"').s(field.getFieldName()).s(": ").c('\"', ')');
      // TODO: another way to retrieve field - e.g. getter?
      dot("append").c('(').thisDot(field.getFieldName()).c(')', ';');
    }

    // ==> result.append('}');
    s("result").dot("append").c('(', '\'').s("}").c('\'', ')', ';');

    // ==> return result.toString();
    s("return").sp().s("result").dot("toString").c('(', ')', ';');

    c('}'); // end of function
  }

  public void generateEquals(Collection<? extends DomainField> fields) {
    final String objectParam = "o";
    final String other = "other";

    annotate(Override.class).s("public").sp().t(boolean.class).sp().s("equals").c('(')
        .t(Object.class).sp().s(objectParam).c(')', ' ', '{');

    // if (this == o) return true;
    s("if").c(' ', '(').s("this").sp().s("==").sp().s(objectParam).c(')', ' ', '{')
        .s("return").sp().s("true").c(';').c('}');

    // if (o == null || getClass() != o.getClass()) return false;
    s("if").c(' ', '(').s(objectParam).sp().s("==").sp().s("null")
        .sp().s("||").sp().s("getClass").c('(', ')').sp().s("!=").sp()
        .dot(objectParam, "getClass").c('(', ')')
        .c(')', ' ', '{').s("return").sp().s("false").c(';').c('}').c('\n');

    // final ClassName other = (ClassName) o;
    s("final").sp().t(getDomainClass()).sp().s(other).c(' ', '=', ' ', '(')
        .t(getDomainClass()).c(')', ' ').s(objectParam).c(';');

    // iterate over the given fields
    for (final DomainField field : fields) {
      // if (...) { return false; }
      s("if").c(' ', '(');

      // if-condition
      generateNonEqualsIfCondition(field, other);

      c(')', ' ', '{').s("return").sp().s("false").c(';', '}');
    }

    // return true
    c('\n').s("return").sp().s("true").c(';');

    c('}'); // end of function
  }

  private void generateHashCode(Collection<? extends DomainField> fields) {
    final String result = "result";
    final String temp = "temp";

    annotate(Override.class).s("public").sp().t(int.class).sp().s("hashCode")
        .c('(', ')', ' ', '{');

    // int result = 0
    t(int.class).sp().s(result).sp().s("=").sp().c('0', ';');
    // additional variable for calculating hash code for doubles
    boolean tempLongRequired = false;
    for (final DomainField field : fields) {
      final Class<?> fieldClass = field.getFieldTypeAsClass();
      if (Double.TYPE.equals(fieldClass)) {
        tempLongRequired = true;
        break;
      }
    }

    if (tempLongRequired) {
      // long temp
      t(long.class).sp().s(temp).c(';');
    }

    // result calculation
    for (final DomainField field : fields) {
      generateHashCodeAddition(field, result, temp);
    }

    // return result;
    s("return").sp().s(result).c(';');

    c('}'); // end of function
  }

  private void generateChecks(DomainField field) {
    // assuming param name equals to the field name
    final String paramName = field.getFieldName();

    // null check
    if (FieldUtil.isNullCheckRequired(field)) {
      s("if").sp().c('(').s(paramName).sp().s("==").sp().s("null").c(')', ' ', '{');
      s("throw").sp().newType(IllegalArgumentException.class).c('(', '\"')
          .s("Parameter '" + paramName + "' is null")
          .c('\"', ')', ';');
      c('}');
    }

    // negative field check
    if (field.hasTrait(FieldTrait.NON_NEGATIVE)) {
      if (!DefaultValues.NUMERIC_PRIMITIVES.contains(field.getFieldTypeAsClass())) {
        // TODO: BigDecimal, BigInteger
        throw new UnsupportedOperationException("Only primitive types supported");
      }

      s("if").sp().c('(').s(paramName).sp().s("<").sp().s("0").c(')', ' ', '{');
      s("throw").sp().newType(IllegalArgumentException.class).c('(', '\"')
          .s("Parameter '" + paramName + "' is null")
          .c('\"', ')', ';');
      c('}');
    }
  }

  private void generateHashCodeAddition(DomainField field, String result, String temp) {
    final Class<?> fieldClass = field.getFieldTypeAsClass();
    final String fieldName = field.getFieldName();

    boolean doubleField = false;
    if (Double.TYPE.equals(fieldClass)) {
      // special case: double field
      doubleField = true;
      // temp = Double.doubleToLongBits(g);
      s(temp).spc('=').t(Double.class).dot("doubleToLongBits").c('(').thisDot(fieldName).c(')', ';');
    }

    // Common code predecessor: result = 31 * result + ...;
    s(result).spc('=').s("31").spc('*').s(result).spc('+');
    if (doubleField) {
      // (int) (temp ^ (temp >>> 32))
      cast(int.class).c('(').s(temp).spc('^')
          .c('(').s(temp).sps(">>>").s("32").c(')', ')');
    } else if (fieldClass != null && fieldClass.isPrimitive()) {
      // special cases for primitive types
      if (Boolean.TYPE.equals(fieldClass)) {
        // (this.field ? 1 : 0)
        c('(').thisDot(fieldName).spc('?').c('1').spc(':').c('0').c(')');
      } else if (Byte.TYPE.equals(fieldClass) || Short.TYPE.equals(fieldClass) || Character.TYPE.equals(fieldClass)) {
        // for byte, short and char: (int) fieldClass
        cast(int.class).thisDot(fieldName);
      } else if (Integer.TYPE.equals(fieldClass)) {
        // int typed - use just member as is
        thisDot(fieldName);
      } else if (Long.TYPE.equals(fieldClass)) {
        // (int) (e ^ (e >>> 32))
        cast(int.class).c('(').thisDot(fieldName).spc('^')
            .c('(').thisDot(fieldName);
        sps(">>>").s("32").c(')', ')');
      } else if (Float.TYPE.equals(fieldClass)) {
        // (f != +0.0f ? Float.floatToIntBits(f) : 0)
        c('(').thisDot(fieldName).sps("!=").s("+0.0f").spc('?')
            .t(Float.class).dot("floatToIntBits").c('(').thisDot(fieldName).c(')').spc(':').c('0', ')');
      } else {
        throw new UnsupportedOperationException("Unsupported primitive type: " + fieldClass);
      }
    } else {
      // object case:
      if (FieldUtil.isNullCheckRequired(field)) {
        // ...=> (this.field != null ? this.field.hashCode() : null)
        c('(').thisDot(fieldName).sps("!=").s("null").spc('?')
            .thisDot(fieldName).dot("hashCode").c('(', ')')
            .spc(':').s("0")
            .c(')');
      } else {
        // ...=> this.field.hashCode()
        thisDot(fieldName).dot("hashCode").c('(', ')');
      }
    }

    c(';');
  }

  private void generateNonEqualsIfCondition(DomainField field, String other) {
    final String fieldName = field.getFieldName();
    final Class<?> fieldClass = field.getFieldTypeAsClass();

    // special logic for primitive members
    if (fieldClass != null && fieldClass.isPrimitive()) {
      // float and double require special comparison
      if (fieldClass.equals(Float.class)) {
        // Float.compare(this.field, other.field) != 0
        t(Float.class).dot("compare").c('(').thisDot(fieldName).c(',', ' ');
        dot(other, fieldName);
        c(')').sps("!=").c('0');
        return;
      } else if (fieldClass.equals(Double.class)) {
        // Double.compare(this.field, other.field) != 0
        t(Float.class).dot("compare").c('(')
            .thisDot(fieldName).c(',', ' ').dot(other, fieldName)
            .c(')').sps("!=").c('0');
        return;
      }

      // this.field != other.field
      thisDot(fieldName).sps("!=").dot(other, fieldName);
      return;
    }

    // generic class case, use equals
    if (FieldUtil.isNullCheckRequired(field)) {
      // this.field != null ? !this.field.equals(other.field) : other.field != null
      thisDot(fieldName).sps("!=").s("null").spc('?')
          .c('!').thisDot(fieldName).dot("equals").c('(').dot(other, fieldName).c(')')
          .spc(':').dot(other, fieldName).sps("!=").s("null");
      return;
    }

    // !this.field.equals(other.equals)
    c('!').thisDot(fieldName).dot("equals").c('(').dot(other, fieldName).c(')');
  }
}
