package com.truward.polymer.domain.analysis;

import com.truward.polymer.domain.DefensiveCopyStyle;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface DomainImplementerSettingsReader {
  @Nonnull
  DefensiveCopyStyle getDefensiveCopyStyle();

  @Nonnull
  String getDefaultTargetPackageName();

  @Nonnull
  String getDefaultImplClassPrefix();

  @Nonnull
  String getDefaultImplClassSuffix();
}
