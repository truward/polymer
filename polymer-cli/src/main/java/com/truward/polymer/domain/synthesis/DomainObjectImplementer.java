package com.truward.polymer.domain.synthesis;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.code.TypeVisitor;
import com.truward.polymer.code.naming.FqName;
import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
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
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * TODO: break down to helper classes
 * @author Alexander Shabanov
 */
public final class DomainObjectImplementer {

  // current generator
  private OutputStreamProvider outputStreamProvider;

  private JavaCodeGenerator generator;

  public DomainObjectImplementer(@Nonnull OutputStreamProvider outputStreamProvider) {
    this.outputStreamProvider = outputStreamProvider;
  }

  public void generateCode(@Nonnull List<DomainImplTarget> implTargets) {
    for (final DomainImplTarget target : implTargets) {
      this.generator = new JavaCodeGenerator();
      generateCompilationUnit(target);
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

  private void generateCompilationUnit(DomainImplTarget target) {
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
      generateField(field);
    }

    // ctor
    generateConstructor(analysisResult, implClassName);

    // getters
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generateFinalGetter(field);
    }

    // setters
    for (final DomainField field : analysisResult.getDeclaredFields()) {
      generateFinalSetter(field);
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

  //
  // Private
  //

  private void generateField(DomainField field) {
    generator.text("private").ch(' ');
    if (!field.hasTrait(SimpleDomainFieldTrait.MUTABLE)) {
      generator.text("final").ch(' ');
    }
    generator.typedVar(field.getFieldType(), field.getFieldName()).ch(';');
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
      generator.type(field.getFieldType());

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
      generateAssignment(field);
    }
    generator.ch('}');
  }

  private void generateAssignment(DomainField field) {
    final JavaCodeGenerator generator = this.generator;
    final String fieldName = field.getFieldName();
    final boolean isMutable = field.hasTrait(SimpleDomainFieldTrait.MUTABLE);
    generator.thisMember(fieldName).spText("=");

    TypeVisitor.apply(new TypeVisitor<Void>() {
      @Override
      public Void visitType(@Nonnull Type sourceType) {
        generator.text(fieldName);
        return null;
      }

      @Override
      public Void visitArray(@Nonnull Type sourceType, @Nonnull Class<?> elementType) {
        // TODO: warning - exposed arrays breaks immutability
        // TODO: Array.copy(fieldName);
        //generator.text("new").ch(' ').type(elementType).ch('[', ']');
        return visitType(sourceType);
      }

      @Override
      public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
        if (List.class.equals(rawType) && !isMutable) {
          // special handling for lists
          // [1] Guava is present - make it optional - otherwise use
          // Collections.unmodifiableList(Arrays.asList({fieldName}.toArray(new {fieldType}[{fieldName}.length])))
          generator.type(ImmutableList.class).ch('.').text("copyOf").ch('(').text(fieldName).ch(')');
          return null;
        }

        return visitType(sourceType);
      }

      @Override
      public Void visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
        return visitType(sourceType);
      }
    }, field.getFieldType());

    generator.ch(';');
  }
}
