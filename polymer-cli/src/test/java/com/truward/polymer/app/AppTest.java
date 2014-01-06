package com.truward.polymer.app;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.truward.polymer.testspec.p1.UserRoleSpecification;
import com.truward.polymer.testspec.p2.UserSpecification;
import com.truward.polymer.testutil.MemOutputStreamProvider;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
  public void shouldGenerateCodeForP1UserRoleSpecification() {
    final MemOutputStreamProvider mosp = new MemOutputStreamProvider();
    App.runCodeGenerator(ImmutableList.<Class<?>>of(UserRoleSpecification.class), mosp);
    System.out.println(String.format("Generated: %d files", mosp.getContentMap().size()));
  }

  @Test
  public void shouldGenerateCodeForP2UserSpecification() {
    final List<String> k = Arrays.asList("ads");
    final List<String> l = Collections.unmodifiableList(Arrays.asList(k.toArray(new String[k.size()])));
    final MemOutputStreamProvider mosp = new MemOutputStreamProvider();
    App.runCodeGenerator(ImmutableList.<Class<?>>of(UserSpecification.class), mosp);
    System.out.println(String.format("Generated: %d files", mosp.getContentMap().size()));
  }
}
