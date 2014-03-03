package com.truward.polymer.core.driver;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Alexander Shabanov
 */
public final class SpecificationUtil {
  /** Utility for working with specification-related entities */
  private SpecificationUtil() {
  }

  public static void notifyState(@Nonnull Collection<? extends SpecificationStateAware> stateAwareBeans,
                                 @Nonnull SpecificationState state) {
    for (final SpecificationStateAware bean : stateAwareBeans) {
      bean.setState(state);
    }
  }
}
