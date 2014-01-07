package com.truward.polymer.core.generator.model;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

/**
 * @author Alexander Shabanov
 */
public final class LocalRefType implements Type, Text {
  private final String name;

  public LocalRefType(@Nonnull String name) {
    this.name = name;
  }

  @Nonnull
  @Override
  public String getText() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LocalRefType that = (LocalRefType) o;

    return name.equals(that.name);

  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return getText();
  }
}
