package com.truward.polymer.app;

import com.google.common.annotations.VisibleForTesting;
import com.truward.di.InjectionContext;
import com.truward.polymer.app.util.ClassScanner;
import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.driver.SpecificationHandler;
import com.truward.polymer.core.output.FSOutputStreamProvider;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.core.support.PolymerModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
      @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
      @Override
      public void visitError(CliOptionsParser.ErrorResult result) {
        System.err.println("Error while running application");
        if (result.getError() != null) {
          System.out.println("Message: " + result.getError());
        }
        if (result.getException() != null) {
          result.getException().printStackTrace(System.err);
        }
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
          System.err.println("Error while generating code");
          if (e.getMessage() != null) {
            System.err.println("Reason: " + e.getMessage());
          }
          e.printStackTrace(System.err);
          System.exit(-1);
        }
      }
    });
  }

  @VisibleForTesting
  public static void generateCode(CliOptionsParser.ProcessSpecResult result) {
    System.out.println("Run App: target=" + result.getTargetDir() +
        ", specificationPackage=" + result.getSpecificationPackage() +
        ", specificationClasses=" + result.getSpecificationClasses());

    final List<Class<?>> specificationClasses;
    if (result.getSpecificationPackage() != null) {
      specificationClasses = ClassScanner.scan(result.getSpecificationPackage());
    } else {
      specificationClasses = new ArrayList<>(result.getSpecificationClasses().size());
      for (final String className : result.getSpecificationClasses()) {
        try {
          specificationClasses.add(Class.forName(className));
        } catch (ClassNotFoundException e) {
          throw new RuntimeException("Unable to load class " + className, e);
        }
      }
    }

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
    final Logger log = LoggerFactory.getLogger(App.class);
    final PolymerModule module = new PolymerModule();
    final InjectionContext injectionContext = module.addDefaults().getInjectionContext();
    injectionContext.registerBean(outputStreamProvider);

    final SpecificationHandler handler = injectionContext.getBean(SpecificationHandler.class);
    for (final Class<?> specificationClass : specificationClasses) {
      handler.parseClass(specificationClass);
    }
    handler.done();

    final List<Implementer> implementers = injectionContext.getBeans(Implementer.class);
    log.debug("Using implementers {} to generate code", implementers);
    for (final Implementer implementer : implementers) {
      implementer.generateImplementations();
    }
  }
}
