package com.truward.polymer.core.naming;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class Name {
  private final String name;

  public Name(@Nonnull String name) {
    this.name = name;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Name name1 = (Name) o;
    return name.equals(name1.name);

  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }
}
