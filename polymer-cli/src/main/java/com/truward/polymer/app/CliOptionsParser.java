package com.truward.polymer.app;

import ch.qos.logback.classic.Level;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command line options parser for polymer application.
 * Thread unsafe, designed for single-threaded use only.
 *
 * TODO: can be generated, eat your own dogfood
 *
 * @author Alexander Shabanov
 */
public final class CliOptionsParser {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final String[] args;
  private int pos;

  public CliOptionsParser(String[] args) {
    this.args = args;
  }

  public Result parse() {
    pos = 0;

    try {
      return doParse();
    } catch (IllegalStateException e) {
      return new ErrorResult(e.getMessage(), e);
    }
  }

  public static void showUsage() {
    System.out.println("Usage:\n" +
        " -h,--help               Shows help\n" +
        " --version               Shows version\n" +
        " -v,--verbose            Produce verbose output\n" +
        " -t,--target {Path}      Specifies path to the target directory\n" +
        " -sp,--specification-package {Package Name}\n" +
        "                         Qualified name of the specification package\n" +
        "                         Should be omitted if --specification-classes option is used\n" +
        " -sc,--specification-classes {Comma-separated List of Strings}\n" +
        "                         Comma-separated list of specification classes\n" +
        "                         Should be omitted if --specification-package option is used\n" +
        "\n");
  }

  //
  // Private
  //

  private Result doParse() {
    String specificationPackage = null;
    String targetDir = null;
    List<String> specificationClasses = new ArrayList<>();
    boolean verbose = false;

    for (pos = 0; pos < args.length; ++pos) {
      if (argEquals("-h", "--help")) {
        return new ShowHelpResult();
      } else if (argEquals("--version")) {
        return new ShowVersionResult();
      } else if (argEquals("--target", "-t")) {
        targetDir = fetchNextArg();
      } else if (argEquals("--verbose", "-v")) {
        verbose = true;
      } else if (argEquals("--specification-package", "-sp")) {
        specificationPackage = fetchNextArg();
      } else if (argEquals("--specification-classes", "-sc")) {
        specificationClasses.addAll(Arrays.asList(fetchNextArg().split(",")));
      } else {
        throw new IllegalStateException("Unknown argument: " + args[pos]);
      }
    }

    if (verbose) {
      System.out.println("Increasing verbosity");
      // enable highest verbosity for the logger
      final Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      ((ch.qos.logback.classic.Logger) rootLogger).setLevel(Level.ALL); // hacky: assuming we're operating w/logback

      log.debug("Starting with verbose output turned on");
    }

    if (specificationPackage == null && specificationClasses.isEmpty()) {
      throw new IllegalStateException("Specification package is not specified and no specification classes were given");
    }

    if (targetDir == null) {
      throw new IllegalStateException("Target directory is not specified");
    }

    return new ProcessSpecResult(specificationClasses, specificationPackage, targetDir);
  }

  private boolean argEquals(String... values) {
    for (final String value : values) {
      if (value.equals(args[pos])) {
        return true;
      }
    }
    return false;
  }

  private String fetchNextArg() {
    if ((pos + 1) >= args.length) {
      throw new IllegalStateException("Argument " + args[pos] + " expects subsequent parameter to be passed");
    }

    return args[++pos];
  }


  //
  // Parsing result
  //

  public interface Result {
    void apply(ResultVisitor visitor);
  }

  public static final class ShowHelpResult implements Result {
    @Override
    public void apply(ResultVisitor visitor) {
      visitor.visitShowHelp(this);
    }
  }

  public static final class ShowVersionResult implements Result {
    @Override
    public void apply(ResultVisitor visitor) {
      visitor.visitShowVersion(this);
    }
  }

  public static final class ErrorResult implements Result {
    private final String error;
    private final Exception exception;

    public ErrorResult(String error, Exception e) {
      this.error = error;
      this.exception = e;
    }

    public String getError() {
      return error;
    }

    public Exception getException() {
      return exception;
    }

    @Override
    public void apply(ResultVisitor visitor) {
      visitor.visitError(this);
    }
  }

  public static final class ProcessSpecResult implements Result {
    private final List<String> specificationClasses;
    private final String specificationPackage;
    private final String targetDir;

    public ProcessSpecResult(List<String> specificationClasses, String specificationPackage, String targetDir) {
      this.specificationClasses = ImmutableList.copyOf(specificationClasses);
      this.specificationPackage = specificationPackage;
      this.targetDir = targetDir;
    }

    public List<String> getSpecificationClasses() {
      return specificationClasses;
    }

    public String getSpecificationPackage() {
      return specificationPackage;
    }

    public String getTargetDir() {
      return targetDir;
    }

    @Override
    public void apply(ResultVisitor visitor) {
      visitor.visitProcessSpec(this);
    }
  }

  public static abstract class ResultVisitor {
    public void visitUnknown(Result result) {
      throw new UnsupportedOperationException("Result should be handled, got: " + result);
    }

    public void visitError(ErrorResult result) {
      visitUnknown(result);
    }

    public void visitShowHelp(ShowHelpResult result) {
      visitUnknown(result);
    }

    public void visitShowVersion(ShowVersionResult result) {
      visitUnknown(result);
    }

    public void visitProcessSpec(ProcessSpecResult result) {
      visitUnknown(result);
    }
  }
}
