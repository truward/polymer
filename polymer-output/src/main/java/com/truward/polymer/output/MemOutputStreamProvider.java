package com.truward.polymer.output;

import com.google.common.collect.ImmutableMap;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Output stream provider, that operates with in-memory streams only.
 *
 * @author Alexander Shabanov
 */
public final class MemOutputStreamProvider implements OutputStreamProvider {
  private final Map<String, String> result = new HashMap<>();

  @Nonnull
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
        result.put(toUnifiedName(name, fileType), bos.toString(DEFAULT_CHARSET.name()));
        bos.close();
      }
    };
  }

  @Nonnull
  public static String toUnifiedName(@Nonnull FqName name, @Nonnull FileType fileType) {
    final StringBuilder builder = new StringBuilder(100);
    try {
      name.appendTo(builder, '/');
    } catch (IOException e) {
      throw new RuntimeException(e); // shouldn't happen
    }
    if (!fileType.getExtension().isEmpty()) {
      builder.append('.').append(fileType.getExtension());
    }
    return builder.toString();
  }
}
