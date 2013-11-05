package com.truward.polymer.domain.synthesis;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;

import java.lang.reflect.Type;

/**
 * @author Alexander Shabanov
 */
public class DomainObjectImplementer {
  private final JavaCodeGenerator generator;
  private final DomainAnalysisResult analysisResult;
  private final String pkgName;
  private final String implClassName;

  public DomainObjectImplementer(JavaCodeGenerator generator, DomainAnalysisResult analysisResult) {
    this.generator = generator;
    this.analysisResult = analysisResult;
    pkgName = "com.mysite.sample"; // TODO: properties
    implClassName = analysisResult.getOriginClass().getSimpleName() + "Impl";
  }

  public void generateCompilationUnit() {
    generator.packageDirective(pkgName);

    generator.textWithSpaces("public", "class", implClassName);
    generator.ch(' ', '{');

    // fields
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generateFinalField(field);
    }

    // ctor
    generateConstructor();

    // getters
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generateFinalGetter(field);
    }

    // toString
    generator.ch('\n');
    ImplementerUtil.generateToString(generator, implClassName, analysisResult.getDeclaredFields());

    // equals
    generator.ch('\n');
    ImplementerUtil.generateEquals(generator, implClassName, analysisResult.getDeclaredFields());

    generator.ch('}'); // end of class body
  }

  //
  // Private
  //

  // TODO: rework this so return type would be Frag.TypeRef
  private String typeRef(Type type) {
    if (type instanceof Class) {
      final Class c = (Class) type;
      return c.getSimpleName();
    } else {
      throw new UnsupportedOperationException(); // TODO: impl
    }
  }

  private void generateFinalField(DomainField field) {
    generator.text("private").ch(' ').text("final").ch(' ').text(typeRef(field.getFieldType())).ch(' ')
        .text(field.getFieldName()).ch(';');
  }

  private void generateFinalGetter(DomainField field) {
    generator.ch('\n').text("public").ch(' ').text("final").ch(' ').text(typeRef(field.getFieldType())).ch(' ')
        .text(field.getGetterName()).ch('(', ')', ' ', '{');
    generator.text("return").ch(' ').text("this").ch('.').text(field.getFieldName()).ch(';');
    generator.ch('}');
  }

  private void generateConstructor() {
    generator.ch('\n');
    generator.text("public").ch(' ').text(implClassName).ch('(');
    boolean next = false;
    // TODO: all fields
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      if (next) {
        generator.ch(',', ' ');
      } else {
        next = true;
      }

      // type
      if (field.getFieldType() instanceof Class) {
        generator.type((Class) field.getFieldType());
      } else {
        throw new UnsupportedOperationException("Implement parameterized types support"); // TODO: impl
      }

      // space and name
      generator.ch(' ').text(field.getFieldName());
    }
    generator.ch(')', ' ', '{');
    // body
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generator.text("this").ch('.').text(field.getFieldName()).ch(' ', '=', ' ').text(field.getFieldName()).ch(';');
    }
    generator.ch('}');
  }
}
