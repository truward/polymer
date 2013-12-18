package com.truward.polymer.app;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.testspec.p1.UserSpecification;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public class AppTest {
  @Test
  public void shouldRunApp() {
    //App.generateCode(new CliOptionsParser.ProcessSpecResult("com.truward.polymer.testspec.p1", "/tmp/testSpec"));
  }

  @Test
  public void shouldGenerateCodeToString() {
    final Map<String, String> result = new HashMap<>();

    App.runCodeGenerator(new App.CodeGeneratorSettings() {
      @Override
      public List<Class<?>> getSpecificationClasses() {
        return ImmutableList.<Class<?>>of(UserSpecification.class);
      }

      @Override
      public String getTargetPackageName() {
        return "com.mysite";
      }

      @Override
      public OutputStream createStreamForFile(final String targetFile) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(500);
        return new OutputStream() {
          @Override
          public void write(int b) throws IOException {
            bos.write(b);
          }

          @Override
          public void close() throws IOException {
            result.put(targetFile, bos.toString("UTF-8"));
            bos.close();
          }
        };
      }
    });

    System.out.println(String.format("Generated: %d files", result.size()));
  }
}
