package com.truward.polymer.core.trait;

import javax.annotation.Nonnull;

/**
 * Helper class for operating on traits.
 *
 * @author Alexander Shabanov
 */
public final class TraitUtil {
  private TraitUtil() {
  }

  public static void incompatibleWith(@Nonnull TraitKey<?> current,
                                      @Nonnull TraitContainer container,
                                      @Nonnull TraitKey<?> other) {
    if (container.hasTrait(other)) {
      throw new RuntimeException("Trait " + current + " is not compatible with " + other + " in " + container);
    }
  }
}
