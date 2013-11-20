package com.truward.polymer.app;

/**
 * Command line options parser for polymer application.
 * Thread unsafe, designed for single-threaded use only.
 *
 * TODO: can be generated, eat your own dogfood
 *
 * @author Alexander Shabanov
 */
public final class CliOptionsParser {
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
      return new ErrorResult(e.getMessage());
    }
  }

  public static void showUsage() {
    System.out.println("Usage:\n" +
        " -h, --help              Shows help\n" +
        " --version               Shows version\n" +
        " -t,--target {Path}      Specifies path to the target directory\n" +
        " -sp,--specification-package {Package}   Qualified name to the\n" +
        "                         specification package\n" +
        "\n");
  }

  //
  // Private
  //

  private Result doParse() {
    String specificationPackage = null;
    String targetDir = null;

    for (pos = 0; pos < args.length; ++pos) {
      if (argEquals("-h", "--help")) {
        return new ShowHelpResult();
      } else if (argEquals("--version")) {
        return new ShowVersionResult();
      } else if (argEquals("--target", "-t")) {
        targetDir = fetchNextArg();
      } else if (argEquals("--specification-package", "-sp")) {
        specificationPackage = fetchNextArg();
      } else {
        throw new IllegalStateException("Unknown argument: " + args[pos]);
      }
    }

    if (specificationPackage == null) {
      throw new IllegalStateException("Specification package is not specified");
    }

    if (targetDir == null) {
      throw new IllegalStateException("Target directory is not specified");
    }

    return new ProcessSpecResult(specificationPackage, targetDir);
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

    public ErrorResult(String error) {
      this.error = error;
    }

    public String getError() {
      return error;
    }

    @Override
    public void apply(ResultVisitor visitor) {
      visitor.visitError(this);
    }
  }

  public static final class ProcessSpecResult implements Result {
    private final String specificationPackage;
    private final String targetDir;

    public ProcessSpecResult(String specificationPackage, String targetDir) {
      this.specificationPackage = specificationPackage;
      this.targetDir = targetDir;
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
