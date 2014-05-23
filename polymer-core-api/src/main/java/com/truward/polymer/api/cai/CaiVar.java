package com.truward.polymer.api.cai;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

/**
 * @author Alexander Shabanov
 */
public interface CaiVar extends CaiAnnotated {

  @Nonnull
  Type getVarType();
}
