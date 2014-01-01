package com.truward.polymer.domain.synthesis;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.code.naming.FqName;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.DomainImplTarget;
import com.truward.polymer.domain.analysis.trait.GetterTrait;
import com.truward.polymer.domain.analysis.trait.SetterTrait;
import com.truward.polymer.domain.analysis.trait.SimpleDomainFieldTrait;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * TODO: break down to helper classes
 * @author Alexander Shabanov
 */
public final class DomainObjectImplementer {

  private DomainObjectImplementer() {
  }

  public static void generateCode(@Nonnull List<DomainImplTarget> implTargets,
                                  @Nonnull OutputStreamProvider outputStreamProvider) {
    for (final DomainImplTarget target : implTargets) {
      final JavaCodeGenerator generator = new JavaCodeGenerator();
      generateCompilationUnit(target, generator);
      try {
        try (final OutputStream stream = outputStreamProvider.createStreamForFile(
            target.getClassName(), DefaultFileTypes.JAVA)) {
          try (final PrintStream printStream = new PrintStream(stream, true, StandardCharsets.UTF_8.name())) {
            generator.printContents(printStream);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static void generateCompilationUnit(DomainImplTarget target, JavaCodeGenerator generator) {
    final FqName classFqName = target.getClassName();
    final DomainAnalysisResult analysisResult = target.getSource();
    final String implClassName = classFqName.getName();

    if (!classFqName.isRoot()) {
      generator.packageDirective(classFqName.getParent());
    }


    generator.textWithSpaces("public", "class", implClassName);
    // implements
    generator.ch(' ').text("implements").ch(' ').type(analysisResult.getOriginClass());
    generator.ch(' ', '{');

    // fields
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generateField(field, generator);
    }

    // ctor
    generateConstructor(analysisResult, implClassName, generator);

    // getters
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generateFinalGetter(field, generator);
    }

    // setters
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generateFinalSetter(field, generator);
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

  private static void generateFinalSetter(DomainField field, JavaCodeGenerator generator) {
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

  //
  // Private
  //

  private static void generateField(DomainField field, JavaCodeGenerator generator) {
    generator.text("private").ch(' ');
    if (!field.hasTrait(SimpleDomainFieldTrait.MUTABLE)) {
      generator.text("final").ch(' ');
    }
    generator.typedVar(field.getFieldType(), field.getFieldName()).ch(';');
  }

  private static void generateFinalGetter(DomainField field, JavaCodeGenerator generator) {
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

  private static void generateConstructor(DomainAnalysisResult analysisResult, String implClassName, JavaCodeGenerator generator) {
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
      generator.thisMember(field.getFieldName()).spText("=").text(field.getFieldName()).ch(';');
    }
    generator.ch('}');
  }
}
