package com.truward.polymer.core.generator.model;

import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class SingleLineComment extends AbstractMultilineComment {
  public SingleLineComment(List<Object> lines) {
    super(lines);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder(200);
    appendLines("// ", builder);
    return builder.toString();
  }
}
