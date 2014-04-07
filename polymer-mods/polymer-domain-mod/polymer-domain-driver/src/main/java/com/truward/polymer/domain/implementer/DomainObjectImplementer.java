package com.truward.polymer.domain.implementer;

import com.truward.polymer.core.code.builder.CodeStream;
import com.truward.polymer.core.code.builder.ModuleBuilder;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.printer.CodePrinter;
import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.core.support.code.DefaultModuleBuilder;
import com.truward.polymer.core.support.code.DefaultTypeManager;
import com.truward.polymer.core.support.code.printer.DefaultCodePrinter;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainImplementationTargetSink;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.freezable.FreezableSupport;
import com.truward.polymer.naming.FqName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

  private Map<DomainAnalysisResult, GenDomainClass> implementationTargets = new HashMap<>();

  @Override
  public void generateImplementations() {
    checkIsFrozen();
    generateCode();
  }

  @Nonnull
  @Override
  public GenDomainClass submit(@Nonnull DomainAnalysisResult analysisResult) {
    checkNonFrozen();
    GenDomainClass genDomainClass = getTarget(analysisResult);
    if (genDomainClass == null) {
      genDomainClass = new GenDomainClass(analysisResult);
      implementationTargets.put(analysisResult, genDomainClass);
    }

    return genDomainClass;
  }

  @Nullable
  @Override
  public GenDomainClass getTarget(@Nonnull DomainAnalysisResult analysisResult) {
    return implementationTargets.get(analysisResult);
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    checkNonFrozen();

    if (state == SpecificationState.COMPLETED) {
      finalizeTargets();
      freeze();
    }
  }

  //
  // Private
  //

  private void finalizeTargets() {
    for (final GenDomainClass implementationTarget : implementationTargets.values()) {
      if (!implementationTarget.hasFqName()) {
        implementationTarget.setFqName(getTargetClassName(implementationTarget.getOrigin()));
      }

      // generate builder class name (if none was set)
      final GenDomainClass.GenBuilderClass builderClass = implementationTarget.getGenBuilderClass();
      if (builderClass.isSupported() && !builderClass.hasFqName()) {
        builderClass.setFqName(new FqName("Builder", implementationTarget.getFqName()));
      }
      // freeze targets class
      implementationTarget.freeze();
    }
  }

  private FqName getTargetClassName(@Nonnull DomainAnalysisResult result) {
    final String className = implementerSettings.getDefaultImplClassPrefix() +
        result.getOriginClass().getSimpleName() + implementerSettings.getDefaultImplClassSuffix();
    return new FqName(className, implementerSettings.getDefaultTargetPackageName());
  }

  private void generateCode() {
    try {
      for (final GenDomainClass implementationTarget : implementationTargets.values()) {
        generateCode(implementationTarget);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    log.info("Done with code generation");
  }

  private void generateCode(@Nonnull GenDomainClass implementationTarget) throws IOException {
    final FqName targetName = implementationTarget.getFqName();
    log.info("Generating file for {}", targetName);

    final TypeManager typeManager = new DefaultTypeManager();
    final ModuleBuilder moduleBuilder = new DefaultModuleBuilder(targetName, typeManager);
    generateCompilationUnit(moduleBuilder.getStream(), implementationTarget);
    moduleBuilder.freeze();

    try (final OutputStream stream = outputStreamProvider.createStreamForFile(targetName, DefaultFileTypes.JAVA)) {
      try (final OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
        final CodePrinter codePrinter = new DefaultCodePrinter(writer, typeManager);
        codePrinter.print(moduleBuilder.getStream());
      }
    }
  }

  private void generateCompilationUnit(@Nonnull CodeStream codeStream, @Nonnull GenDomainClass domainClass) {
    final ClassImplementer classImplementer = new ClassImplementer(codeStream, domainClass, implementerSettings);

    // compilation unit generation
    classImplementer.generateHead();
    if (domainClass.getGenBuilderClass().isSupported()) {
      final BuilderImplementer builderImplementer = new BuilderImplementer(codeStream, domainClass);
      builderImplementer.generateInnerBuilder();
    }

    classImplementer.generateEpilogue();
  }
}
