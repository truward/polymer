package com.truward.polymer.app;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.truward.polymer.testutil.MemOutputStreamProvider;
import com.truward.polymer.testspec.p1.UserSpecification;
import org.junit.Test;

/**
 * @author Alexander Shabanov
 */
public class AppTest {
  @Test
  public void shouldRunApp() {
    if (!"true".equals(System.getProperty("polymer.integration.test.run"))) {
      return;
    }

    final String outputPath = Files.createTempDir().getName();
    App.generateCode(new CliOptionsParser.ProcessSpecResult("com.truward.polymer.testspec.p1", outputPath));
  }

  @Test
  public void shouldGenerateCodeToString() {
    final MemOutputStreamProvider mosp = new MemOutputStreamProvider();
    App.runCodeGenerator(ImmutableList.<Class<?>>of(UserSpecification.class), mosp);
    System.out.println(String.format("Generated: %d files", mosp.getContentMap().size()));
  }
}
