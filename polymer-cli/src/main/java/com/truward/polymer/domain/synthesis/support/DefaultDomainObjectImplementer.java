package com.truward.polymer.domain.synthesis.support;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.domain.analysis.DomainImplementationTarget;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;
import com.truward.polymer.domain.synthesis.DomainObjectImplementer;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
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
  @Resource
  private DomainImplementerSettingsReader implementerSettings;

  @Resource
  private OutputStreamProvider outputStreamProvider;


  @Override
  public void generateCode(@Nonnull List<DomainImplementationTarget> targets) {
    for (final DomainImplementationTarget target : targets) {
      final JavaCodeGenerator generator = new JavaCodeGenerator();
      generateCompilationUnit(generator, target);
      try {
        try (final OutputStream stream = outputStreamProvider.createStreamForFile(target.getTargetName(), DefaultFileTypes.JAVA)) {
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

  private void generateCompilationUnit(@Nonnull JavaCodeGenerator generator, @Nonnull DomainImplementationTarget target) {
    final ClassImplementer classImplementer = new ClassImplementer(generator, implementerSettings, target);
    final Type implClass = classImplementer.getTargetClass();
    final BuilderImplementer builderImplementer = new BuilderImplementer(generator, implClass,
        target.getAnalysisResult());
    final JacksonSupportImplementer jacksonSupportImplementer = new JacksonSupportImplementer(generator, implClass,
        target.getAnalysisResult());

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
}
