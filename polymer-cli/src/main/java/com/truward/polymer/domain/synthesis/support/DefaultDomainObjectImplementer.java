package com.truward.polymer.domain.synthesis.support;

import com.truward.polymer.code.naming.FqName;
import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;
import com.truward.polymer.domain.synthesis.DomainObjectImplementer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class DefaultDomainObjectImplementer implements DomainObjectImplementer {

  @Override
  public void generateCode(@Nonnull OutputStreamProvider outputStreamProvider,
                           @Nonnull DomainImplementerSettingsReader implementerSettings,
                           @Nonnull List<DomainAnalysisResult> implTargets) {
    for (final DomainAnalysisResult target : implTargets) {
      final JavaCodeGenerator generator = new JavaCodeGenerator();
      final FqName classFqName = getImplClassName(implementerSettings, target);
      generateCompilationUnit(implementerSettings, generator, target, classFqName);
      try {
        try (final OutputStream stream = outputStreamProvider.createStreamForFile(classFqName, DefaultFileTypes.JAVA)) {
          try (final PrintStream printStream = new PrintStream(stream, true, StandardCharsets.UTF_8.name())) {
            generator.printContents(printStream);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  //
  // Private
  //

  private static void generateCompilationUnit(@Nonnull DomainImplementerSettingsReader implementerSettings,
                                              @Nonnull JavaCodeGenerator generator,
                                              @Nonnull DomainAnalysisResult target,
                                              @Nonnull FqName classFqName) {
    final ClassImplementer classImplementer = new ClassImplementer(generator, implementerSettings, target, classFqName);
    final Type implClass = classImplementer.getImplClass();
    final BuilderImplementer builderImplementer = new BuilderImplementer(generator, implClass, target);
    final JacksonSupportImplementer jacksonSupportImplementer = new JacksonSupportImplementer(generator, implClass, target);

    // compilation unit generation
    classImplementer.generateHeaderAndPrologue();
    // TODO: non-inner builder support
    if (builderImplementer.isInnerBuilderSupported()) {
      builderImplementer.generateInnerBuilder();
    }

    // TODO: non-inner jackson support
    if (jacksonSupportImplementer.isInnerCreatorSupported()) {
      jacksonSupportImplementer.generateInnerCreator();
    }
    classImplementer.generateEpilogue();
  }

  private static FqName getImplClassName(@Nonnull DomainImplementerSettingsReader implementerSettings,
                                         @Nonnull DomainAnalysisResult result) {
    final StringBuilder nameBuilder = new StringBuilder(200);
    nameBuilder.append(implementerSettings.getDefaultTargetPackageName()).append('.');
    nameBuilder.append(implementerSettings.getDefaultImplClassPrefix())
        .append(result.getOriginClass().getSimpleName())
        .append(implementerSettings.getDefaultImplClassSuffix());

    return FqName.parse(nameBuilder.toString());
  }
}
