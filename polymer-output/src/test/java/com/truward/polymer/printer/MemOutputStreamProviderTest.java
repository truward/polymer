package com.truward.polymer.printer;

import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.FileType;
import com.truward.polymer.output.MemOutputStreamProvider;
import com.truward.polymer.output.OutputStreamProvider;
import com.truward.polymer.output.StandardFileTypes;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link com.truward.polymer.output.MemOutputStreamProvider}
 *
 * @author Alexander Shabanov
 */
public final class MemOutputStreamProviderTest {
  private MemOutputStreamProvider provider;
  private final String content = "content";

  @Before
  public void init() {
    provider = new MemOutputStreamProvider();
  }

  @Test
  public void shouldAppendFileType() throws IOException {
    print(FqName.parse("my.File"), StandardFileTypes.JAVA, content);
    assertEquals(content, provider.getContentMap().get("my/File.java"));
  }

  @Test
  public void shouldOmitExtension() throws IOException {
    print(FqName.parse("README"), StandardFileTypes.NONE, content);
    assertEquals(content, provider.getContentMap().get("README"));
  }

  //
  // Private
  //

  private void print(FqName name, FileType fileType, String content) throws IOException {
    try (final OutputStream out = provider.createStreamForFile(name, fileType)) {
      try (final OutputStreamWriter writer = new OutputStreamWriter(out, OutputStreamProvider.DEFAULT_CHARSET)) {
        writer.append(content);
      }
    }
  }
}
