package com.truward.polymer.core.util;

import com.truward.polymer.core.code.builder.CodeFactory;
import com.truward.polymer.core.code.untyped.GenString;

import javax.annotation.Nonnull;

/**
 * Utility class, that manages variable names, takes variable name parameter and
 * returns variable name suffixed with the proper index.
 *
 * @author Alexander Shabanov
 */
public final class VarNameManager {
  private final String baseName;
  private final CodeFactory codeFactory;
  private int index = 0;

  public VarNameManager(@Nonnull String baseName, @Nonnull CodeFactory codeFactory) {
    this.baseName = baseName;
    this.codeFactory = codeFactory;
  }

  @Nonnull
  public GenString nextName() {
    assert index >= 0;
    String name = baseName;
    if (index > 0) {
      name = baseName + index;
    }
    ++index;
    return codeFactory.newString(name);
  }

  public void releaseName() {
    --index;
    assert index >= 0;
  }
}
