package com.truward.polymer.api.output;

import com.truward.polymer.freezable.Freezable;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.FileType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author Alexander Shabanov
 */
public interface VirtualFile extends Freezable {

  @Nonnull
  FqName getName();

  @Nonnull
  FileType getType();

  @Nonnull
  OutputStream getOutputStream() throws IOException;

  @Nonnull
  Writer getWriter() throws IOException;

  boolean isFrozen();
}
