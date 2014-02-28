package com.truward.polymer.core.code.printer;

import com.truward.polymer.core.code.GenObject;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Alexander Shabanov
 */
public interface CodePrinter {
  void print(@Nonnull GenObject object) throws IOException;
}
