package com.truward.polymer.core.output;

import com.google.common.collect.ImmutableMap;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class MemOutputStreamProvider implements OutputStreamProvider {
  private final Map<String, String> result = new HashMap<>();

  public Map<String, String> getContentMap() {
    return ImmutableMap.copyOf(result);
  }

  @Override
  @Nonnull
  public OutputStream createStreamForFile(@Nonnull final FqName name,
                                          @Nonnull final FileType fileType) throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream(500);
    return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        bos.write(b);
      }

      @Override
      public void close() throws IOException {
        result.put(toUnifiedName(name, fileType), bos.toString("UTF-8"));
        bos.close();
      }
    };
  }

  public static String toUnifiedName(@Nonnull FqName name, @Nonnull FileType fileType) {
    return name.toString() + '.' + fileType.getExtension();
  }
}
