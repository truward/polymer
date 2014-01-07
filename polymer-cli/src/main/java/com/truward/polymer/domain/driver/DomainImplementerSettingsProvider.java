package com.truward.polymer.domain.driver;

import com.truward.polymer.code.freezable.FreezableSupport;
import com.truward.polymer.domain.DefensiveCopyStyle;
import com.truward.polymer.domain.DomainImplementerSettings;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class DomainImplementerSettingsProvider extends FreezableSupport implements DomainImplementerSettings {
  private DefensiveCopyStyle defensiveCopyStyle = DefensiveCopyStyle.JDK;

  @Override
  public void setDefensiveCopyStyle(@Nonnull DefensiveCopyStyle defensiveCopyStyle) {
    checkNonFrozen();
    this.defensiveCopyStyle = defensiveCopyStyle;
  }

  @Nonnull
  public DefensiveCopyStyle getDefensiveCopyStyle() {
    return defensiveCopyStyle;
  }
}
