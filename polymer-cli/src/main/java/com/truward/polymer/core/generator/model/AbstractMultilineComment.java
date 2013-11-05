package com.truward.polymer.core.generator.model;

import java.util.List;

/**
 * @author Alexander Shabanov
 */
abstract class AbstractMultilineComment implements CodeObject {
  private final List<Object> lines;

  public AbstractMultilineComment(List<Object> lines) {
    this.lines = lines;
  }

  public final List<Object> getLines() {
    return lines;
  }

  @Override
  public final boolean equals(Object o) {
    return (o != null) && (getClass() == o.getClass()) && lines.equals(((AbstractMultilineComment) o).getLines());
  }

  @Override
  public final int hashCode() {
    return lines != null ? lines.hashCode() : 0;
  }

  protected final void appendLines(String prefix, StringBuilder result) {
    for (final Object line : getLines()) {
      result.append(prefix).append(line);
    }
  }
}
