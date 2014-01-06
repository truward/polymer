package com.truward.polymer.domain.driver;

import com.truward.polymer.domain.DefensiveCopyStyle;
import com.truward.polymer.domain.DomainImplementerSettings;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class DomainImplementerSettingsProvider implements DomainImplementerSettings {
  private DefensiveCopyStyle defensiveCopyStyle = DefensiveCopyStyle.JDK;

  @Override
  public void setDefensiveCopyStyle(@Nonnull DefensiveCopyStyle defensiveCopyStyle) {
    this.defensiveCopyStyle = defensiveCopyStyle;
  }

  @Nonnull
  public DefensiveCopyStyle getDefensiveCopyStyle() {
    return defensiveCopyStyle;
  }
}
