package com.truward.polymer.domain.synthesis.support;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;

/**
 * Simple implementer that is capable to generate implementations of equals and hashCode based on the given fields.
 * The generated implementer will call getters.
 *
 * @author Alexander Shabanov
 */
public final class ImplementerUtil {
  private ImplementerUtil() {}

  public static void generateField(@Nonnull JavaCodeGenerator generator, @Nonnull DomainField field, boolean isFinal) {
    generator.text("private").ch(' ');
    if (!isFinal) {
      generator.text("final").ch(' ');
    }
    generator.typedVar(field.getFieldType(), field.getFieldName()).ch(';');
  }
}
