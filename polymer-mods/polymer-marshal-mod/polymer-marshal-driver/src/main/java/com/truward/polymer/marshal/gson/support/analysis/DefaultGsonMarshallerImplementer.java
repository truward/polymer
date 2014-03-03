package com.truward.polymer.marshal.gson.support.analysis;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.code.builder.CodeStream;
import com.truward.polymer.core.code.builder.CodeStreamSupport;
import com.truward.polymer.core.code.builder.ModuleBuilder;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.printer.CodePrinter;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.core.support.code.DefaultModuleBuilder;
import com.truward.polymer.core.support.code.DefaultTypeManager;
import com.truward.polymer.core.support.code.printer.DefaultCodePrinter;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.gson.analysis.GsonTarget;
import com.truward.polymer.marshal.gson.analysis.GsonMarshallerImplementer;
import com.truward.polymer.naming.FqName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class DefaultGsonMarshallerImplementer extends FreezableSupport
    implements GsonMarshallerImplementer, SpecificationStateAware {
  private final Logger log = LoggerFactory.getLogger(DefaultGsonMarshallerImplementer.class);

  private final Map<GenDomainClass, GsonTarget> domainClassToGsonTarget = new HashMap<>();

  @Resource
  private OutputStreamProvider outputStreamProvider;

  @Override
  public void submit(@Nonnull GenDomainClass domainClass) {
    checkNonFrozen();

    if (domainClassToGsonTarget.containsKey(domainClass)) {
      log.info("Duplicate submission of domain class {}", domainClass);
      return;
    }

    domainClassToGsonTarget.put(domainClass, new DefaultGsonTarget(domainClass));
  }

  @Override
  public void generateImplementations() {
    try {
      final FqName targetName = FqName.parse("com.target.Foo");
      log.info("Generating file for {}", targetName);

      // prepare module generator
      final TypeManager typeManager = new DefaultTypeManager();
      final ModuleBuilder moduleBuilder = new DefaultModuleBuilder(targetName.getParent(), typeManager);

      // generate code
      final GsonBinderImplementer binderImplementer = new GsonBinderImplementer(targetName, moduleBuilder.getStream(),
          domainClassToGsonTarget.values());
      binderImplementer.generate();

      // freeze generated code
      moduleBuilder.freeze();

      // dump code to the file
      try (final OutputStream stream = outputStreamProvider.createStreamForFile(targetName, DefaultFileTypes.JAVA)) {
        try (final OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
          final CodePrinter codePrinter = new DefaultCodePrinter(writer, typeManager);
          codePrinter.print(moduleBuilder.getStream());
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    log.info("Done with GSON code generation");
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    if (state == SpecificationState.COMPLETED) {
      checkNonFrozen();

      finalizeAnalysis();
      freeze();
    }
  }

  //
  // Private
  //

  private void finalizeAnalysis() {
    // TODO: impl
    log.info("Gson marshaller generation has been completed");
  }

  private static final class GsonBinderImplementer extends CodeStreamSupport {
    private final CodeStream codeStream;
    private final List<GsonTarget> targets;
    private final FqName fqName;

    private GsonBinderImplementer(@Nonnull FqName fqName, @Nonnull CodeStream codeStream,
                                  @Nonnull Collection<GsonTarget> targets) {
      this.fqName = fqName;
      this.codeStream = codeStream;
      this.targets = ImmutableList.copyOf(targets);
    }

    @Nonnull
    @Override
    protected CodeStream getRootCodeStream() {
      return codeStream;
    }

    public void generate() {
      s("public").sps("final").s("class").sps(fqName.getName()).c('{');

      for (final GsonTarget target : targets) {
        s("// method for " + target.getDomainClass().getOrigin().getOriginClass()).eol();
      }

      c('}'); // class body end
    }
  }
}
