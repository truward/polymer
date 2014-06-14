package com.truward.polymer.api.output;

import com.truward.polymer.freezable.Freezable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Alexander Shabanov
 */
public interface VirtualFile extends Freezable {

  @Nonnull
  Writer getWriter() throws IOException;

  boolean isFrozen();

  boolean isVoid();
}
