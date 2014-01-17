package com.truward.polymer.core.generator.model;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface Printable extends CodeObject {
  void print(@Nonnull CodeObjectPrinter out);
}
