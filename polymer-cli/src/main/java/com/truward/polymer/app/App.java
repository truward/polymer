package com.truward.polymer.app;

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
        System.out.println("Error: " + result.getError());
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
        // TODO: run application
        System.out.println("Run App: target=" + result.getTargetDir() + ", sp=" + result.getSpecificationPackage());
      }
    });
  }

  private static void generateCode() {

  }
}
