package com.truward.polymer.api.impl.output;

import com.truward.polymer.api.output.VirtualFile;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Represents a real file on a physical device.
 *
 * @author Alexander Shabanov
 */
public final class PhysicalFile implements VirtualFile {
  private File file;

  public PhysicalFile(@Nonnull String pathname) {
    file = new File(pathname);
  }

  @Nonnull
  @Override
  public Writer getWriter() throws IOException {
    if (isFrozen()) {
      throw new IllegalStateException("This writer is frozen");
    }
    final FileOutputStream fileOutputStream = new FileOutputStream(file);
    return new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
  }

  @Override
  public boolean isFrozen() {
    return file == null;
  }

  @Override
  public boolean isVoid() {
    return false;
  }

  @Override
  public void freeze() {
    file = null;
  }
}
