package com.truward.polymer.domain;

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
   * @param field Field, which is specified to be nullable
   * @return Current specifier, provided for convenience such that the user is able to chain calls.
   */
  @Nonnull
  DomainObjectSpecifier isNullable(Object field);

  @Nonnull
  DomainObjectSpecifier isNonNull(Object field);

  @Nonnull
  DomainObjectSpecifier hasLength(String field);

  @Nonnull
  DomainObjectSpecifier isNonNegative(int field);
}
