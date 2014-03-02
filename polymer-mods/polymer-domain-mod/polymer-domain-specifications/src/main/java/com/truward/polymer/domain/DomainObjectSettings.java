package com.truward.polymer.domain;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * Represents additional code generation settings for the particular domain object.
 *
 * @author Alexander Shabanov
 */
public interface DomainObjectSettings {
  DomainObjectBuilderSettings assignBuilder();

  void setTargetName(@Nonnull FqName implementationName);
}
