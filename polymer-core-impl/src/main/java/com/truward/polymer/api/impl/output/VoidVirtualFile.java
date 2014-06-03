package com.truward.polymer.api.impl.output;

import com.truward.polymer.api.output.ContentGenerator;
import com.truward.polymer.api.output.VirtualFile;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class VoidVirtualFile implements VirtualFile {
  public static final VoidVirtualFile INSTANCE = new VoidVirtualFile();

  private VoidVirtualFile() {
  }

  @Nonnull
  @Override
  public ContentGenerator getContentGenerator() {
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
