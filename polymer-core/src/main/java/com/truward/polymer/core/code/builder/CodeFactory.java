package com.truward.polymer.core.code.builder;

import com.truward.polymer.core.code.untyped.GenChar;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.code.untyped.GenString;

import javax.annotation.Nonnull;

/**
 * Factory methods for creating different node instances
 *
 * @author Alexander Shabanov
 */
public interface CodeFactory {
  @Nonnull GenInlineBlock newInlineBlock();

  @Nonnull GenString newString(@Nonnull String s);

  @Nonnull GenChar newChar(char c);
}
