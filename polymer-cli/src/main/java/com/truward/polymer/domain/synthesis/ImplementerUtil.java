package com.truward.polymer.domain.synthesis;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.domain.analysis.DomainField;

import java.util.Collection;

/**
 * Simple implementer that is capable to generate implementations of equals and hashCode based on the given fields.
 * The generated implementer will call getters.
 *
 * @author Alexander Shabanov
 */
public final class ImplementerUtil {

  public static void generateToString(JavaCodeGenerator g, String className, Collection<? extends DomainField> fields) {
    g.ch('@').type(Override.class).ch('\n');
    g.text("public").ch(' ').type(String.class).ch(' ').text("toString").ch('(', ')', ' ', '{');

    // ==> final StringBuilder result = new StringBuilder();
    g.text("final").ch(' ').type(StringBuilder.class).ch(' ').text("result").ch(' ', '=', ' ')
        .text("new").ch(' ').type(StringBuilder.class).ch('(', ')', ';');

    // ==> result.append("ClassName#{");
    g.text("result").dot("append").ch('(', '\"').text(className + "#{").ch('\"', ')', ';');

    // fields
    boolean next = false;
    for (final DomainField field : fields) {
      g.text("result");
      if (next) {
        g.dot("append").ch('(', '\"').text(", ").ch('\"', ')');
      } else {
        next = true;
      }

      g.dot("append").ch('(', '\"').text(field.getFieldName()).text(": ").ch('\"', ')');
      g.dot("append").ch('(').text(field.getGetterName()).ch('(', ')', ')', ';');
    }

    // ==> result.append('}');
    g.text("result").dot("append").ch('(', '\'').text("}").ch('\'', ')', ';');

    // ==> return result.toString();
    g.text("return").ch(' ').text("result").dot("toString").ch('(', ')', ';');

    g.ch('}'); // end of function
  }

  public static void generateEquals(JavaCodeGenerator g, String className, Collection<? extends DomainField> fields) {
    final String objectParam = "o";
    final String other = "other";

    g.ch('@').type(Override.class).ch('\n');
    g.text("public").ch(' ').type(boolean.class).ch(' ').text("equals").ch('(')
        .type(Object.class).ch(' ').text(objectParam).ch(')', ' ', '{');

    // if (this == o) return true;
    g.text("if").ch(' ', '(').text("this").spText("==").text(objectParam).ch(')', ' ', '{')
        .text("return").ch(' ').text("true").ch(';').ch('}');

    // if (o == null || getClass() != o.getClass()) return false;
    g.text("if").ch(' ', '(').text(objectParam).spText("==").text("null")
        .spText("||").text("getClass").ch('(', ')').spText("!=")
        .member(objectParam, "getClass").ch('(', ')')
        .ch(')', ' ', '{').text("return", "false").ch(';').ch('}').ch('\n');

    // final ClassName other = (ClassName) o;
    g.text("final").ch(' ').text(className).ch(' ').text(other).ch(' ', '=', ' ', '(')
        .text(className).ch(')', ' ').text(other).ch(';');

    // iterate over the given fields
    for (final DomainField field : fields) {
      // if (...) { return false; }
      g.text("if").ch(' ', '(');

      // if-condition
      generateNonEqualsIfCondition(g, field, other);

      g.ch(')', ' ', '{').text("return", "false").ch(';', '}');
    }

    // return true
    g.ch('\n').text("return", "true").ch(';');

    g.ch('}'); // end of function
  }

  public static void generateHashCode(JavaCodeGenerator g, Collection<? extends DomainField> fields) {
    final String result = "result";
    final String temp = "temp";

    g.ch('@').type(Override.class).ch('\n');
    g.text("public").ch(' ').type(int.class).dot("hashCode").ch('(', ')', ' ', '{');

    // int result = 0
    g.type(int.class).ch(' ').text(result).spText("=").ch('0', ';');
    // additional variable for calculating hash code for doubles
    boolean tempLongRequired = false;
    for (final DomainField field : fields) {
      final Class<?> fieldClass = field.getFieldTypeAsClass();
      if (Double.class.equals(fieldClass)) {
        tempLongRequired = true;
        break;
      }
    }

    if (tempLongRequired) {
      // long temp
      g.type(long.class).ch(' ').text(temp).ch(';');
    }

    // result calculation
    for (final DomainField field : fields) {
      generateHashCodeAddition(g, field, result, temp);
    }

    // return result;
    g.text("return", result).ch(';');

    g.ch('}'); // end of function
  }


  //
  // Private
  //

  private static void generateHashCodeAddition(JavaCodeGenerator g, DomainField field, String result, String temp) {
    final Class<?> fieldClass = field.getFieldTypeAsClass();
    final String fieldName = field.getFieldName();

    boolean doubleField = false;
    if (Double.TYPE.equals(fieldClass)) {
      // special case: double field
      doubleField = true;
      // temp = Double.doubleToLongBits(g);
      g.text(temp).spText("=").type(Double.class).dot("doubleToLongBits").ch('(').thisMember(fieldName).ch(')', ';');
    }

    // Common code predecessor: result = 31 * result + ...;
    g.text(result).spText("=").text("31").spText("*").text(result).spText("+");
    if (doubleField) {
      // (int) (temp ^ (temp >>> 32))
      g.cast(int.class).ch('(').text(temp).spText("^")
          .ch('(').text(temp).spText(">>>").text("32").ch(')', ')');
    } else if (fieldClass != null && fieldClass.isPrimitive()) {
      // special cases for primitive types
      if (Boolean.TYPE.equals(fieldClass)) {
        // (this.field ? 1 : 0)
        g.ch('(').thisMember(fieldName).spText("?").ch('1').spText(":").ch('0').ch(')');
      } else if (Byte.TYPE.equals(fieldClass) || Short.TYPE.equals(fieldClass) || Character.TYPE.equals(fieldClass)) {
        // for byte, short and char: (int) fieldClass
        g.cast(int.class).thisMember(fieldName);
      } else if (Integer.TYPE.equals(fieldClass)) {
        // int type - use just member as is
        g.thisMember(fieldName);
      } else if (Long.TYPE.equals(fieldClass)) {
        // (int) (e ^ (e >>> 32))
        g.cast(int.class).ch('(').thisMember(fieldName).spText("^")
            .ch('(').thisMember(fieldName).spText(">>>").text("32").ch(')', ')');
      } else if (Float.TYPE.equals(fieldClass)) {
        // (f != +0.0f ? Float.floatToIntBits(f) : 0)
        g.ch('(').thisMember(fieldName).spText("!=").text("+0.0f").spText("?")
            .type(Float.class).dot("floatToIntBits").ch('(').thisMember(fieldName).ch(')').spText(":").ch('0', ')');
      } else {
        throw new UnsupportedOperationException("Unsupported primitive type: " + fieldClass);
      }
    } else {
      // object case:
      if (field.isNullable()) {
        // ...=> (this.field != null ? this.field.hashCode() : null)
        g.ch('(').thisMember(fieldName).spText("!=").text("null").spText("?")
            .thisMember(fieldName).dot("hashCode").ch('(', ')')
            .spText(":").text("null")
            .ch(')');
      } else {
        // ...=> this.field.hashCode()
        g.thisMember(fieldName).dot("hashCode").ch('(', ')');
      }
    }

    g.ch(';');
  }

  private static void generateNonEqualsIfCondition(JavaCodeGenerator g, DomainField field, String other) {
    final String fieldName = field.getFieldName();
    final Class<?> fieldClass = field.getFieldTypeAsClass();

    // special logic for primitive members
    if (fieldClass != null && fieldClass.isPrimitive()) {
      // float and double require special comparison
      if (fieldClass.equals(Float.class)) {
        // Float.compare(this.field, other.field) != 0
        g.type(Float.class).dot("compare").ch('(')
            .thisMember(fieldName).ch(',', ' ').member(other, fieldName)
            .ch(')').spText("!=").ch('0');
        return;
      } else if (fieldClass.equals(Double.class)) {
        // Double.compare(this.field, other.field) != 0
        g.type(Float.class).dot("compare").ch('(')
            .thisMember(fieldName).ch(',', ' ').member(other, fieldName)
            .ch(')').spText("!=").ch('0');
        return;
      }

      // this.field != other.field
      g.thisMember(fieldName).spText("!=").member(other, fieldName);
      return;
    }

    // generic class case, use equals
    if (field.isNullable()) {
      // this.field != null ? !this.field.equals(other.field) : other.field != null
      g.thisMember(fieldName).spText("!=").text("null").spText("?")
          .ch('!').thisMember(fieldName).dot("equals").ch('(').member(other, fieldName).ch(')')
          .spText(":").member(other, fieldName).spText("!=").text("null");
      return;
    }

    // !this.field.equals(other.equals)
    g.ch('!').thisMember(fieldName).dot("equals").ch('(').member(other, fieldName).ch(')');
  }
}
