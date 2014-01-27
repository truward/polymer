package com.truward.polymer.domain;

import com.truward.polymer.annotation.SpecificatorInvocation;

import javax.annotation.Nonnull;

/**
 * Specification service for configuring domain object generation for the given classes.
 *
 * @author Alexander Shabanov
 */
public interface DomainObjectSpecifier {
  @Nonnull
  <T> T domainObject(@Nonnull Class<T> clazz);

  /**
   * Specifies, that the given field is nullable.
   * By default all the fields are non-nullable.
   *
   * @param invocationResult Getter invocation that triggers information about the corresponding field
   * @return Current specifier, provided for convenience such that the user is able to chain calls.
   */
  @Nonnull
  DomainObjectSpecifier isNullable(@SpecificatorInvocation Object invocationResult);

  @Nonnull
  DomainObjectSpecifier isNonNull(@SpecificatorInvocation Object invocationResult);

  @Nonnull
  DomainObjectSpecifier hasLength(@SpecificatorInvocation String invocationResult);

  @Nonnull
  DomainObjectSpecifier isNonNegative(@SpecificatorInvocation int invocationResult);

  @Nonnull
  DomainObjectSettings getObjectSettings(@Nonnull Class<?> clazz);

  @Nonnull
  DomainImplementerSettings getImplementerSettings();
}
