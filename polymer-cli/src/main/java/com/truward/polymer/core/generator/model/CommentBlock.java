package com.truward.polymer.core.generator.model;


import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class CommentBlock extends AbstractMultilineComment {
  public CommentBlock(List<Object> lines) {
    super(lines);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder(200);
    builder.append("/**");
    appendLines(" * ", builder);
    builder.append(" */");
    return builder.toString();
  }
}
