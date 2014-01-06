package com.truward.polymer.domain;

import javax.annotation.Nonnull;

/**
 * Generic settings for the domain object generator.
 *
 * @author Alexander Shabanov
 */
public interface DomainGeneratorSettings {
  void setDefensiveCopyStyle(@Nonnull DefensiveCopyStyle defensiveCopyStyle);
}
