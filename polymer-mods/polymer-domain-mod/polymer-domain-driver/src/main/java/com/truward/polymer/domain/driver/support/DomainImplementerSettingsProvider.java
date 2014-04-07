package com.truward.polymer.domain.driver.support;

import com.truward.polymer.domain.DefensiveCopyStyle;
import com.truward.polymer.domain.DomainImplementerSettings;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;
import com.truward.polymer.freezable.FreezableSupport;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class DomainImplementerSettingsProvider extends FreezableSupport
    implements DomainImplementerSettings, DomainImplementerSettingsReader {
  private DefensiveCopyStyle defensiveCopyStyle = DefensiveCopyStyle.JDK;
  private FqName defaultTargetPackageName = FqName.parse("generated");
  private String defaultImplClassPrefix = "";
  private String defaultImplClassSuffix = "Impl";

  @Override
  public void setDefensiveCopyStyle(@Nonnull DefensiveCopyStyle defensiveCopyStyle) {
    checkNonFrozen();
    this.defensiveCopyStyle = defensiveCopyStyle;
  }

  @Override
  public void setTargetPackageName(@Nonnull FqName defaultTargetPackageName) {
    checkNonFrozen();
    this.defaultTargetPackageName = defaultTargetPackageName;
  }

  @Override
  public void setImplClassPrefix(@Nonnull String prefix) {
    checkNonFrozen();
    this.defaultImplClassPrefix = prefix;
  }

  @Nonnull
  @Override
  public DefensiveCopyStyle getDefensiveCopyStyle() {
    return defensiveCopyStyle;
  }

  @Override
  public void setImplClassSuffix(@Nonnull String suffix) {
    checkNonFrozen();
    this.defaultImplClassSuffix = suffix;
  }

  @Nonnull
  @Override
  public FqName getDefaultTargetPackageName() {
    return defaultTargetPackageName;
  }

  @Nonnull
  @Override
  public String getDefaultImplClassPrefix() {
    return defaultImplClassPrefix;
  }

  @Nonnull
  @Override
  public String getDefaultImplClassSuffix() {
    return defaultImplClassSuffix;
  }
}
