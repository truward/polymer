package com.truward.polymer.api.cai;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface CaiInterface extends CaiAnnotated {

  @Nullable
  Class<?> getOriginJavaInterface();

  /**
   * @return Immediate childs
   */
  @Nonnull
  List<CaiAnnotated> getChilds();

  @Nonnull
  List<CaiInterface> getParents();
}
