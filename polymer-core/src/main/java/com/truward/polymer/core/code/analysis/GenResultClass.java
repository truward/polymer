package com.truward.polymer.core.code.analysis;

import com.truward.polymer.core.code.typed.GenEmergentClass;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public abstract class GenResultClass<T extends AnalysisResult> extends GenEmergentClass {
  private final T origin;

  protected GenResultClass(@Nonnull T origin) {
    this.origin = origin;
  }

  @Nonnull
  public T getOrigin() {
    return origin;
  }

  @Override
  public String toString() {
    return "GenResultClass<" + getFqName() + ">#Origin:" + getOrigin() + "";
  }
}
