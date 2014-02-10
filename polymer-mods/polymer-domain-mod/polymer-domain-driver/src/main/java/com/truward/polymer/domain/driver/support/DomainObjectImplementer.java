package com.truward.polymer.domain.driver.support;

import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.core.util.Assert;
import com.truward.polymer.core.util.TargetTrait;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainImplementationTargetSink;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;
import com.truward.polymer.naming.FqName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private final Logger log = LoggerFactory.getLogger(DomainObjectImplementer.class);

  @Resource
  private DomainImplementerSettingsReader implementerSettings;

  @Resource
  private OutputStreamProvider outputStreamProvider;

  private List<DomainAnalysisResult> implementationTargets = new ArrayList<>();

  @Override
  public void generateImplementations() {
    if (!isFrozen()) {
      throw new IllegalStateException("Implementer has not been frozen, completion has not been occured");
    }
    generateCode(implementationTargets);
  }

  @Override
  public void submit(@Nonnull DomainAnalysisResult analysisResult) {
    analysisResult.putTrait(new TargetTrait());
    implementationTargets.add(analysisResult);
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    checkNonFrozen();

    if (state == SpecificationState.COMPLETED) {
      for (final DomainAnalysisResult target : implementationTargets) {
        Assert.nonNull(target.findTrait(TargetTrait.KEY)).setTargetName(getTargetClassName(target));
      }

      freeze();
    }
  }

  //
  // Private
  //

  private FqName getTargetClassName(@Nonnull DomainAnalysisResult result) {
    final String className = implementerSettings.getDefaultImplClassPrefix() +
        result.getOriginClass().getSimpleName() + implementerSettings.getDefaultImplClassSuffix();
    return new FqName(className, implementerSettings.getDefaultTargetPackageName());
  }

  private void generateCode(@Nonnull List<DomainAnalysisResult> analysisResults) {
    for (final DomainAnalysisResult analysisResult : analysisResults) {
      final TargetTrait targetTrait = Assert.nonNull(analysisResult.findTrait(TargetTrait.KEY));
      final JavaCodeGenerator generator = new JavaCodeGenerator();
      generateCompilationUnit(generator, analysisResult);
      try {
        final FqName targetName = targetTrait.getTargetName();
        log.info("Generating file for {}", targetName);

        try (final OutputStream stream = outputStreamProvider.createStreamForFile(targetName, DefaultFileTypes.JAVA)) {
          try (final PrintStream printStream = new PrintStream(stream, true, StandardCharsets.UTF_8.name())) {
            generator.printContents(printStream);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    log.info("Done with code generation");
  }

  private void generateCompilationUnit(@Nonnull JavaCodeGenerator generator, @Nonnull DomainAnalysisResult analysisResult) {
    final ClassImplementer classImplementer = new ClassImplementer(generator, implementerSettings, analysisResult);
    final Type implClass = classImplementer.getTargetClass();
    final BuilderImplementer builderImplementer = new BuilderImplementer(generator, implClass, analysisResult);

    // compilation unit generation
    classImplementer.generateHeaderAndPrologue();
    // TODO: non-inner builder support
    if (builderImplementer.isInnerBuilderSupported()) {
      builderImplementer.generateInnerBuilder();
    }

    classImplementer.generateEpilogue();
  }
}
