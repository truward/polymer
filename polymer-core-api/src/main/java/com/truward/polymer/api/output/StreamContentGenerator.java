package com.truward.polymer.api.output;

import javax.annotation.Nonnull;
import java.io.Writer;

/**
 * Simple stream content generator for text files.
 * UTF-8 encoding will be used by default.
 *
 * @author Alexander Shabanov
 */
public interface StreamContentGenerator extends ContentGenerator {

  @Nonnull
  Writer getWriter();
}
