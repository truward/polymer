package com.truward.polymer.domain;

/**
 * Service, that is capable to specify
 *
 * @author Alexander Shabanov
 */
public interface DomainObjectSpecifier {
  <T> T domainObject(Class<T> clazz);

  DomainObjectSpecifier isNullable(Object field);

  DomainObjectSpecifier hasLength(String field);

  DomainObjectSpecifier isNonNegative(int field);
}
