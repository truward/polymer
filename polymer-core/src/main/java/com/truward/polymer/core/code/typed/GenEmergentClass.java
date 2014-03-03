package com.truward.polymer.core.code.typed;

import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * Base for emergent(generated) classes.
 *
 * @author Alexander Shabanov
 */
public abstract class GenEmergentClass extends FreezableSupport implements GenClass {
  private FqName fqName;

  @Override
  public final boolean isPrimitive() {
    return false;
  }

  @Override
  public final void setFqName(@Nonnull FqName name) {
    checkNonFrozen();
    fqName = name;
  }

  @Override
  public final boolean hasFqName() {
    return fqName != null;
  }

  @Nonnull
  @Override
  public final FqName getFqName() {
    if (fqName == null) {
      throw new IllegalStateException("Can't get fully qualified name");
    }
    return fqName;
  }
}
