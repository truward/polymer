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

  public static void generateToString(JavaCodeGenerator generator,
                                      String className,
                                      Collection<? extends DomainField> fields) {
    generator.ch('@').type(Override.class).ch('\n');
    generator.text("public").ch(' ').type(String.class).ch(' ').text("toString").ch('(', ')', ' ', '{');

    // ==> final StringBuilder result = new StringBuilder();
    generator.text("final").ch(' ').type(StringBuilder.class).ch(' ').text("result").ch(' ', '=', ' ')
        .text("new").ch(' ').type(StringBuilder.class).ch('(', ')', ';');

    // ==> result.append("ClassName#{");
    generator.text("result").ch('.').text("append").ch('(', '\"').text(className + "#{").ch('\"', ')', ';');

    // fields
    boolean next = false;
    for (final DomainField field : fields) {
      generator.text("result");
      if (next) {
        generator.ch('.').text("append").ch('(', '\"').text(", ").ch('\"', ')');
      } else {
        next = true;
      }

      generator.ch('.').text("append").ch('(', '\"').text(field.getFieldName()).text(": ").ch('\"', ')');
      generator.ch('.').text("append").ch('(').text(field.getGetterName()).ch('(', ')', ')', ';');
    }

    // ==> result.append('}');
    generator.text("result").ch('.').text("append").ch('(', '\'').text("}").ch('\'', ')', ';');

    // ==> return result.toString();
    generator.text("return").ch(' ').text("result").ch('.').text("toString").ch('(', ')', ';');

    generator.ch('}');
  }
}
