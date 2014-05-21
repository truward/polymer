package com.truward.polymer.domain.analysis;

import com.truward.polymer.plugin.domain.DefensiveCopyStyle;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface DomainImplementerSettingsReader {
  @Nonnull
  DefensiveCopyStyle getDefensiveCopyStyle();

  @Nonnull
  FqName getDefaultTargetPackageName();

  @Nonnull
  String getDefaultImplClassPrefix();

  @Nonnull
  String getDefaultImplClassSuffix();
}
