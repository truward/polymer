package com.truward.polymer.api.impl.output;

import com.truward.polymer.api.output.VirtualFile;

import javax.annotation.Nonnull;
import java.io.Writer;

/**
 * @author Alexander Shabanov
 */
public final class VoidVirtualFile implements VirtualFile {
  private static final VoidVirtualFile INSTANCE = new VoidVirtualFile();

  private VoidVirtualFile() {
  }

  @Nonnull
  public static VoidVirtualFile getInstance() {
    return INSTANCE;
  }

  @Nonnull
  @Override
  public Writer getWriter() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isFrozen() {
    return true;
  }

  @Override
  public boolean isVoid() {
    return true;
  }

  @Override
  public void freeze() {
    // ok
  }

  @Override
  public String toString() {
    return "<VoidVirtualFile>";
  }
}
