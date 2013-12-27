package com.truward.polymer.app;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.truward.polymer.testutil.MemOutputStreamProvider;
import com.truward.polymer.testspec.p1.UserSpecification;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Alexander Shabanov
 */
public class AppTest {
  @Test
  public void shouldRunApp() throws IOException {
    if (!Boolean.valueOf(System.getProperty("polymer.integration.test.run"))) {
      return;
    }

    final String outputPath;
    if (Boolean.valueOf(System.getProperty("polymer.integration.test.useSysTemp"))) {
      outputPath = Files.createTempDir().getCanonicalPath();
    } else {
      outputPath = "/tmp/polymer_target_" + Long.toHexString(System.currentTimeMillis());
    }

    App.generateCode(new CliOptionsParser.ProcessSpecResult("com.truward.polymer.testspec.p1", outputPath));
  }

  @Test
  public void shouldGenerateCodeToString() {
    final MemOutputStreamProvider mosp = new MemOutputStreamProvider();
    App.runCodeGenerator(ImmutableList.<Class<?>>of(UserSpecification.class), mosp);
    System.out.println(String.format("Generated: %d files", mosp.getContentMap().size()));
  }
}
