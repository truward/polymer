package com.truward.polymer.app;

import com.google.common.annotations.VisibleForTesting;
import com.truward.di.InjectionContext;
import com.truward.polymer.app.util.ClassScanner;
import com.truward.polymer.core.driver.SpecificationHandler;
import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.domain.analysis.DomainImplTargetProvider;
import com.truward.polymer.domain.synthesis.DomainObjectImplementer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Main entry point.
 */
public final class App {
  public static void main(String[] args) {
    final CliOptionsParser parser = new CliOptionsParser(args);
    final CliOptionsParser.Result result = parser.parse();
    result.apply(new CliOptionsParser.ResultVisitor() {
      @Override
      public void visitError(CliOptionsParser.ErrorResult result) {
        System.err.println("Error: " + result.getError());
        CliOptionsParser.showUsage();
        System.exit(-1);
      }

      @Override
      public void visitShowHelp(CliOptionsParser.ShowHelpResult result) {
        CliOptionsParser.showUsage();
      }

      @Override
      public void visitShowVersion(CliOptionsParser.ShowVersionResult result) {
        // TODO: fetch version from resources
        System.out.println("Version: 0.0.1-SNAPSHOT");
      }

      @Override
      public void visitProcessSpec(CliOptionsParser.ProcessSpecResult result) {
        try {
          generateCode(result);
        } catch (RuntimeException e) {
          System.err.append("Error: ").println(e.getMessage());
          System.exit(-1);
        }
      }
    });
  }

  @VisibleForTesting
  public static void generateCode(CliOptionsParser.ProcessSpecResult result) {
    // TODO: run application
    System.out.println("Run App: target=" + result.getTargetDir() + ", sp=" + result.getSpecificationPackage());

    final List<Class<?>> specificationClasses = ClassScanner.scan(result.getSpecificationPackage());
    if (specificationClasses.isEmpty()) {
      throw new RuntimeException("No classes to analyze");
    }

    runCodeGenerator(new CodeGeneratorSettings() {
      @Override
      public List<Class<?>> getSpecificationClasses() {
        return specificationClasses;
      }

      @Override
      public OutputStream createStreamForFile(String targetFile) throws IOException {
        throw new UnsupportedOperationException();
      }
    });
  }

  @VisibleForTesting
  public static void runCodeGenerator(CodeGeneratorSettings settings) {
    final PolymerModule module = new PolymerModule();
    final InjectionContext injectionContext = module.addDefaults().getInjectionContext();
    final SpecificationHandler handler = injectionContext.getBean(SpecificationHandler.class);

    for (final Class<?> specificationClass : settings.getSpecificationClasses()) {
      handler.parseClass(specificationClass);
    }

    // TODO: wire implementer
    final DomainObjectImplementer implementer = new DomainObjectImplementer(
        injectionContext.getBean(JavaCodeGenerator.class),
        injectionContext.getBean(DomainImplTargetProvider.class).getImplementationTargets()
    );
    implementer.generateCode();
  }

  public interface CodeGeneratorSettings {
    List<Class<?>> getSpecificationClasses();

    OutputStream createStreamForFile(String targetFile) throws IOException;
  }
}
