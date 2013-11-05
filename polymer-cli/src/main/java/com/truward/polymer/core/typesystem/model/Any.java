package com.truward.polymer.core.typesystem.model;

/**
 * @author Alexander Shabanov
 */
public final class Any implements TypeParameter {
  public Any() {
  }

  public static final Any INSTANCE = new Any();

  @Override
  public int hashCode() {
    return 37;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Any;
  }

  @Override
  public String toString() {
    return "?";
  }
}
