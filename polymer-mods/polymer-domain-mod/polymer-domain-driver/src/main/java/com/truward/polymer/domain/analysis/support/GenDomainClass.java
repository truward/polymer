package com.truward.polymer.domain.analysis.support;

import com.truward.polymer.core.code.analysis.GenResultClass;
import com.truward.polymer.core.code.typed.GenEmergentClass;
import com.truward.polymer.domain.DomainObjectBuilderSettings;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;

import javax.annotation.Nonnull;

/**
 * Represents a domain class
 *
 * @author Alexander Shabanov
 */
public final class GenDomainClass extends GenResultClass<DomainAnalysisResult> {
  private final GenBuilderClass genBuilderClass = new GenBuilderClass();

  public GenDomainClass(@Nonnull DomainAnalysisResult origin) {
    super(origin);
  }

  @Nonnull
  public GenBuilderClass getGenBuilderClass() {
    return genBuilderClass;
  }

  @Override
  protected void beforeFreezing() {
    genBuilderClass.freeze();
    super.beforeFreezing();
  }

  //
  // Exposed classes
  //

  public static final class GenBuilderClass extends GenEmergentClass implements DomainObjectBuilderSettings {
    private boolean supported;

    public boolean isSupported() {
      return supported;
    }

    public void setSupported(boolean supported) {
      checkNonFrozen();
      this.supported = supported;
    }
  }
}
