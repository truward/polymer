package com.truward.polymer.marshal.jackson.support;

import com.truward.polymer.core.code.builder.ModuleBuilder;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.printer.CodePrinter;
import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenClassReference;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.core.support.code.DefaultModuleBuilder;
import com.truward.polymer.core.support.code.DefaultTypeManager;
import com.truward.polymer.core.support.code.printer.DefaultCodePrinter;
import com.truward.polymer.core.util.Assert;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.json.analysis.JsonMarshallerImplementer;
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
public final class DefaultJacksonMarshallerImplementer extends FreezableSupport
    implements JsonMarshallerImplementer, SpecificationStateAware {
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final Map<GenDomainClass, JsonTarget> domainClassToJsonTarget = new HashMap<>();
  private final Map<JsonTarget, GenClass> serializerClasses = new HashMap<>();
  private final Map<JsonTarget, GenClass> deserializerClasses = new HashMap<>();

  @Resource
  private OutputStreamProvider outputStreamProvider;

  private FqName targetClassName;
  private boolean mappersRequired;

  public DefaultJacksonMarshallerImplementer() {
    // TODO: pick somewhere from the default settings?
    this.mappersRequired = true;
  }

  @Override
  public void submit(@Nonnull GenDomainClass domainClass) {
    checkNonFrozen();

    if (domainClassToJsonTarget.containsKey(domainClass)) {
      log.info("Duplicate submission of domain class {}", domainClass);
      return;
    }

    domainClassToJsonTarget.put(domainClass, new DefaultJsonTarget(domainClass));
  }

  @Override
  public void generateImplementations() {
    try {
      log.info("Generating file for {}", targetClassName);

      // prepare module generator
      final TypeManager typeManager = new DefaultTypeManager();
      final ModuleBuilder moduleBuilder = new DefaultModuleBuilder(targetClassName, typeManager);

      // generate code
      final JacksonBinderImplementer binderImplementer = new JacksonBinderImplementer(targetClassName,
          moduleBuilder.getStream(), domainClassToJsonTarget);
      binderImplementer.generate();

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

  //
  // Private
  //

  private void finalizeAnalysis() {
    Assert.nonNull(targetClassName, "Target class name expected to be non-null");

    if (mappersRequired) {
      for (final JsonTarget target : domainClassToJsonTarget.values()) {
        final String simpleName = target.getDomainClass().getOrigin().getOriginClass().getSimpleName();

        if (target.isReaderSupportRequested() && !deserializerClasses.containsKey(target)) {
          deserializerClasses.put(target, GenClassReference.from(new FqName(simpleName + "Deserializer", targetClassName)));
        }

        if (target.isWriterSupportRequested() && !serializerClasses.containsKey(target)) {
          serializerClasses.put(target, GenClassReference.from(new FqName(simpleName + "Serializer", targetClassName)));
        }
      }
    }

    log.info("Json marshaller analysis has been completed");
  }
}
