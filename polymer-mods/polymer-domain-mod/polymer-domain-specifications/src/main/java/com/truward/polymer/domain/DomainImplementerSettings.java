package com.truward.polymer.domain;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * Generic settings for the domain object generator.
 *
 * @author Alexander Shabanov
 */
public interface DomainImplementerSettings {
  void setDefensiveCopyStyle(@Nonnull DefensiveCopyStyle defensiveCopyStyle);

  void setDefaultTargetPackageName(@Nonnull FqName defaultTargetPackageName);

  void setDefaultImplClassPrefix(@Nonnull String prefix);

  void setDefaultImplClassSuffix(@Nonnull String suffix);
}
