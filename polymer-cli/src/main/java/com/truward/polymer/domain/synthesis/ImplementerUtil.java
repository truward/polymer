package com.truward.polymer.domain.synthesis;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.domain.analysis.DomainField;

import java.lang.reflect.Type;
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
    g.text("result").ch('.').text("append").ch('(', '\"').text(className + "#{").ch('\"', ')', ';');

    // fields
    boolean next = false;
    for (final DomainField field : fields) {
      g.text("result");
      if (next) {
        g.ch('.').text("append").ch('(', '\"').text(", ").ch('\"', ')');
      } else {
        next = true;
      }

      g.ch('.').text("append").ch('(', '\"').text(field.getFieldName()).text(": ").ch('\"', ')');
      g.ch('.').text("append").ch('(').text(field.getGetterName()).ch('(', ')', ')', ';');
    }

    // ==> result.append('}');
    g.text("result").ch('.').text("append").ch('(', '\'').text("}").ch('\'', ')', ';');

    // ==> return result.toString();
    g.text("return").ch(' ').text("result").ch('.').text("toString").ch('(', ')', ';');

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


  //
  // Private
  //

  private static void generateNonEqualsIfCondition(JavaCodeGenerator g, DomainField field, String other) {
    final Type fieldType = field.getFieldType();
    final String fieldName = field.getFieldName();
    if (fieldType instanceof Class) {
      final Class fieldClass = (Class) fieldType;
      if (fieldClass.isPrimitive()) {
        // float and double require special comparison
        if (fieldClass.equals(Float.class)) {
          // Float.compare(this.field, other.field) != 0
          g.type(Float.class).ch('.').text("compare").ch('(')
              .thisMember(fieldName).ch(',', ' ').member(other, fieldName)
              .ch(')').spText("!=").ch('0');
          return;
        } else if (fieldClass.equals(Double.class)) {
          // Double.compare(this.field, other.field) != 0
          g.type(Float.class).ch('.').text("compare").ch('(')
              .thisMember(fieldName).ch(',', ' ').member(other, fieldName)
              .ch(')').spText("!=").ch('0');
          return;
        }

        // this.field != other.field
        g.thisMember(fieldName).spText("!=").member(other, fieldName);
        return;
      }
    }

    // generic class case, use equals
    if (field.isNullable()) {
      // this.field != null ? !this.field.equals(other.field) : other.field != null
      g.thisMember(fieldName).spText("!=").text("null").spText("?")
          .ch('!').thisMember(fieldName).ch('.').text("equals").ch('(').member(other, fieldName).ch(')')
          .spText(":")
          .member(other, fieldName).spText("!=").text("null");
      return;
    }

    // !this.field.equals(other.equals)
    g.ch('!').thisMember(fieldName).ch('.').text("equals").ch('(').member(other, fieldName).ch(')');
  }
}
