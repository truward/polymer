package com.truward.polymer.core.code.typed;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * Represents a reference to the external class - which may not be defined in JVM
 * at the moment of use.
 *
 * @author Alexander Shabanov
 */
public final class GenClassReference implements GenClass {
  private final FqName name;

  private GenClassReference(@Nonnull FqName name) {
    this.name = name;
  }

  @Nonnull
  public static GenClassReference from(@Nonnull FqName fqName) {
    return new GenClassReference(fqName);
  }

  @Nonnull
  public static GenClassReference from(@Nonnull String fqName) {
    return from(FqName.parse(fqName));
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public void setFqName(@Nonnull FqName name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasFqName() {
    return true;
  }

  @Nonnull
  @Override
  public FqName getFqName() {
    return name;
  }
}
