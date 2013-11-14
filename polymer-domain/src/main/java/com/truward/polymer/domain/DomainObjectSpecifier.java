package com.truward.polymer.domain;

/**
 * Specification service for configuring domain object generation for the given classes.
 *
 * @author Alexander Shabanov
 */
public interface DomainObjectSpecifier {
  <T> T domainObject(Class<T> clazz);

  /**
   * Specifies, that the given field is nullable.
   * By default all the fields are non-nullable.
   *
   * @param field Field, which is specified to be nullable
   * @result Current specifier, provided for convenience such that the user is able to chain calls.
   */
  DomainObjectSpecifier isNullable(Object field);

  DomainObjectSpecifier hasLength(String field);

  DomainObjectSpecifier isNonNegative(int field);
}
