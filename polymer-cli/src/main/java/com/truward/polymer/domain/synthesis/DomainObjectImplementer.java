package com.truward.polymer.domain.synthesis;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.naming.FqName;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.DomainImplTarget;

import java.util.List;

/**
 * @author Alexander Shabanov
 */
public class DomainObjectImplementer {
  private final JavaCodeGenerator generator;
  private List<DomainImplTarget> implTargets;

  public DomainObjectImplementer(JavaCodeGenerator generator, List<DomainImplTarget> implTargets) {
    this.generator = generator;
    this.implTargets = implTargets;
  }

  public void generateCode() {
    for (final DomainImplTarget target : implTargets) {
      generateCompilationUnit(target);
    }
  }

  private void generateCompilationUnit(DomainImplTarget target) {
    final FqName classFqName = target.getClassName();
    final DomainAnalysisResult analysisResult = target.getSource();
    final String implClassName = classFqName.getName();

    if (!classFqName.isRoot()) {
      generator.packageDirective(classFqName.getParent().toString());
    }


    generator.textWithSpaces("public", "class", implClassName);
    // implements
    generator.ch(' ').text("implements").ch(' ').type(analysisResult.getOriginClass());
    generator.ch(' ', '{');

    // fields
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generateFinalField(field);
    }

    // ctor
    generateConstructor(analysisResult, implClassName);

    // getters
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generateFinalGetter(field);
    }

    // toString
    generator.ch('\n');
    ImplementerUtil.generateToString(generator, implClassName, analysisResult.getDeclaredFields());

    // hashCode
    generator.ch('\n');
    ImplementerUtil.generateHashCode(generator, analysisResult.getDeclaredFields());

    // equals
    generator.ch('\n');
    ImplementerUtil.generateEquals(generator, implClassName, analysisResult.getDeclaredFields());

    generator.ch('}'); // end of class body
  }

  //
  // Private
  //

  private void generateFinalField(DomainField field) {
    generator.text("private").ch(' ').text("final").ch(' ').type(field.getFieldType()).ch(' ')
        .text(field.getFieldName()).ch(';');
  }

  private void generateFinalGetter(DomainField field) {
    generator.ch('\n')
        .annotate(Override.class).text("public").ch(' ').text("final").ch(' ').type(field.getFieldType()).ch(' ')
        .text(field.getGetterName()).ch('(', ')', ' ', '{');
    generator.text("return").ch(' ').text("this").ch('.').text(field.getFieldName()).ch(';');
    generator.ch('}');
  }

  private void generateConstructor(DomainAnalysisResult analysisResult, String implClassName) {
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
        generator.type(field.getFieldType());
      } else {
        throw new UnsupportedOperationException("Implement parameterized types support"); // TODO: impl
      }

      // space and name
      generator.ch(' ').text(field.getFieldName());
    }
    generator.ch(')', ' ', '{');

    // verification of the input arguments
    // TODO: optional?
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      ImplementerUtil.generateNullCheckIfNeeded(generator, field);
    }

    // body
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generator.text("this").ch('.').text(field.getFieldName()).spText("=").text(field.getFieldName()).ch(';');
    }
    generator.ch('}');
  }
}
