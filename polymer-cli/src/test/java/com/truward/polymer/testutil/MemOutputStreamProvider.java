package com.truward.polymer.testutil;

import com.google.common.collect.ImmutableMap;
import com.truward.polymer.core.generator.OutputStreamProvider;
import com.truward.polymer.core.naming.FqName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Shabanov
 */
public final class MemOutputStreamProvider implements OutputStreamProvider {
  private final Map<String, String> result = new HashMap<>();

  public Map<String, String> getContentMap() {
    return ImmutableMap.copyOf(result);
  }

  public String getOneContent() {
    assertEquals(1, result.size());
    return result.values().iterator().next();
  }

  @Override
  public OutputStream createStreamForFile(final FqName name, final String extension) throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream(500);
    return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        bos.write(b);
      }

      @Override
      public void close() throws IOException {
        result.put(toUnifiedName(name, extension), bos.toString("UTF-8"));
        bos.close();
      }
    };
  }

  public static String toUnifiedName(FqName name, String extension) {
    return name.toString() + '.' + extension;
  }
}
