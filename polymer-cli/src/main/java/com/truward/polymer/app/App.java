package com.truward.polymer.app;

import com.google.common.annotations.VisibleForTesting;
import com.truward.di.InjectionContext;
import com.truward.polymer.app.util.ClassScanner;
import com.truward.polymer.core.PolymerModule;
import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.driver.SpecificationHandler;
import com.truward.polymer.core.output.FSOutputStreamProvider;
import com.truward.polymer.core.output.OutputStreamProvider;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Main entry point.
 */
public final class App {
  public static void main(String[] args) {
//    if (new File("/tmp/dbg").exists()) {
//      args = new String[] {
//          "--version"
//      };
//    }

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
        final Properties properties = new Properties();
        try {
          final InputStream inputStream = getClass().getResourceAsStream("/app.properties");
          if (inputStream == null) {
            throw new IOException("No app.properties");
          }
          properties.load(inputStream);
          final String version = properties.getProperty("polymer.cli.app.version");
          // TODO: fetch version from resources
          System.out.println("Version: " + version);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
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
    System.out.println("Run App: target=" + result.getTargetDir() + ", specificationPackage=" + result.getSpecificationPackage());

    final List<Class<?>> specificationClasses = ClassScanner.scan(result.getSpecificationPackage());
    if (specificationClasses.isEmpty()) {
      throw new RuntimeException("No classes to analyze");
    }

    try {
      runCodeGenerator(new FSOutputStreamProvider(new File(result.getTargetDir())), specificationClasses);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @VisibleForTesting
  public static void runCodeGenerator(@Nonnull OutputStreamProvider outputStreamProvider,
                                      @Nonnull List<Class<?>> specificationClasses) {
    final PolymerModule module = new PolymerModule();
    final InjectionContext injectionContext = module.addDefaults().getInjectionContext();
    injectionContext.registerBean(outputStreamProvider);

    final SpecificationHandler handler = injectionContext.getBean(SpecificationHandler.class);
    for (final Class<?> specificationClass : specificationClasses) {
      handler.parseClass(specificationClass);
    }
    handler.done();

    final List<Implementer> implementers = injectionContext.getBeans(Implementer.class);
    for (final Implementer implementer : implementers) {
      implementer.generateImplementations();
    }
  }
}
