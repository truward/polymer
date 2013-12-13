package com.truward.polymer.app;

import org.junit.Test;

/**
 * @author Alexander Shabanov
 */
public class AppTest {
  @Test
  public void shouldRunApp() {
    App.generateCode(new CliOptionsParser.ProcessSpecResult("com.truward.polymer.testspec.p1", "/tmp/testSpec"));
  }
}
