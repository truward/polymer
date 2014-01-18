package com.truward.polymer.domain.driver.support;

import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.naming.FqName;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainImplementationTarget;
import com.truward.polymer.domain.analysis.DomainImplementationTargetSink;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;
import com.truward.polymer.domain.analysis.support.DefaultDomainImplementationTarget;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class DomainObjectImplementer extends FreezableSupport implements Implementer,
    DomainImplementationTargetSink, SpecificationStateAware {
  @Resource
  private DomainImplementerSettingsReader implementerSettings;

  @Resource
  private OutputStreamProvider outputStreamProvider;

  private List<DomainImplementationTarget> implementationTargets = new ArrayList<>();

  @Override
  public void generateImplementations() {
    if (!isFrozen()) {
      throw new IllegalStateException("Implementer has not been frozen, completion has not been occured");
    }
    generateCode(implementationTargets);
  }

  @Override
  public void submit(@Nonnull DomainAnalysisResult analysisResult) {
    implementationTargets.add(new DefaultDomainImplementationTarget(analysisResult));
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    checkNonFrozen();

    if (state == SpecificationState.COMPLETED) {
      for (final DomainImplementationTarget target : implementationTargets) {
        target.setTargetName(getTargetClassName(target.getAnalysisResult()));
      }

      freeze();
    }
  }

  //
  // Private
  //

  private FqName getTargetClassName(@Nonnull DomainAnalysisResult result) {
    final StringBuilder nameBuilder = new StringBuilder(200);
    nameBuilder.append(implementerSettings.getDefaultTargetPackageName()).append('.');
    nameBuilder.append(implementerSettings.getDefaultImplClassPrefix())
        .append(result.getOriginClass().getSimpleName())
        .append(implementerSettings.getDefaultImplClassSuffix());

    return FqName.parse(nameBuilder.toString());
  }

  private void generateCode(@Nonnull List<DomainImplementationTarget> targets) {
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

  private void generateCompilationUnit(@Nonnull JavaCodeGenerator generator, @Nonnull DomainImplementationTarget target) {
    final ClassImplementer classImplementer = new ClassImplementer(generator, implementerSettings, target);
    final Type implClass = classImplementer.getTargetClass();
    final BuilderImplementer builderImplementer = new BuilderImplementer(generator, implClass,
        target.getAnalysisResult());

    // compilation unit generation
    classImplementer.generateHeaderAndPrologue();
    // TODO: non-inner builder support
    if (builderImplementer.isInnerBuilderSupported()) {
      builderImplementer.generateInnerBuilder();
    }

    classImplementer.generateEpilogue();
  }
}
