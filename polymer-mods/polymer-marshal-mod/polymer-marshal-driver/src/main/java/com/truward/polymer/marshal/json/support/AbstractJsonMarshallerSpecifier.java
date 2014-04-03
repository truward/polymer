package com.truward.polymer.marshal.json.support;

import com.truward.polymer.core.code.builder.ModuleBuilder;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.printer.CodePrinter;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.core.support.code.DefaultModuleBuilder;
import com.truward.polymer.core.support.code.DefaultTypeManager;
import com.truward.polymer.core.support.code.printer.DefaultCodePrinter;
import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainImplementationTargetSink;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.json.JsonMarshallingSpecifier;
import com.truward.polymer.marshal.json.analysis.JsonTarget;
import com.truward.polymer.marshal.json.support.analysis.DefaultJsonTarget;
import com.truward.polymer.naming.FqName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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
public abstract class AbstractJsonMarshallerSpecifier extends FreezableSupport
    implements JsonMarshallingSpecifier, Implementer, SpecificationStateAware, Freezable {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  @Resource
  private DomainImplementationTargetSink implementationTargetSink;

  @Resource
  private DomainAnalysisContext analysisContext;

  @Resource
  private OutputStreamProvider outputStreamProvider;

  private final Map<GenDomainClass, JsonTarget> domainClassToJsonTarget = new HashMap<>();
  private FqName targetClassName;

  public FqName getTargetClassName() {
    return targetClassName;
  }

  public Map<GenDomainClass, JsonTarget> getDomainClassToJsonTarget() {
    return domainClassToJsonTarget;
  }

  @Override
  public final void generateImplementations() {
    try {
      log.info("Generating file for {}", targetClassName);

      // prepare module generator
      final TypeManager typeManager = new DefaultTypeManager();
      final ModuleBuilder moduleBuilder = new DefaultModuleBuilder(targetClassName, typeManager);

      // generate code
      generateCode(moduleBuilder.getStream());

      // freeze generated code
      moduleBuilder.freeze();

      // dump code to the file
      try (final OutputStream stream = outputStreamProvider.createStreamForFile(targetClassName, DefaultFileTypes.JAVA)) {
        try (final OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
          final CodePrinter codePrinter = new DefaultCodePrinter(writer, typeManager);
          codePrinter.print(moduleBuilder.getStream());
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    log.info("Done with Jackson marshallers generation");
  }

  @Override
  public final JsonMarshallingSpecifier setGeneratorTarget(@Nonnull FqName targetClass) {
    checkNonFrozen();
    this.targetClassName = targetClass;
    return this;
  }

  @Override
  public final JsonMarshallingSpecifier addDomainEntity(@Nonnull Class<?> entityClass) {
    checkNonFrozen();
    // should be reentrant-safe
    final GenDomainClass domainClass = implementationTargetSink.getTarget(analysisContext.analyze(entityClass));
    if (domainClass == null) {
      throw new IllegalStateException("Can't generate gson target for class that has no implementation target: " +
          entityClass);
    }
    submit(domainClass);
    return this;
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    if (state == SpecificationState.COMPLETED) {
      if (targetClassName == null) {
        // TODO: exception?
        targetClassName = FqName.parse("generated.JsonMarshaller");
      }

      checkNonFrozen();

      finalizeAnalysis();
      freeze();
    }
  }

  protected abstract void finalizeAnalysis();

  protected abstract void generateCode(GenInlineBlock bodyStream);

  //
  // Private
  //

  private void submit(GenDomainClass domainClass) {
    checkNonFrozen();

    if (domainClassToJsonTarget.containsKey(domainClass)) {
      log.info("Duplicate submission of domain class {}", domainClass);
      return;
    }

    domainClassToJsonTarget.put(domainClass, new DefaultJsonTarget(domainClass));
  }
}
