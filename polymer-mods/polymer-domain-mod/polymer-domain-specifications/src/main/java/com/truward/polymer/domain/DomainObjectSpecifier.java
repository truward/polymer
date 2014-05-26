package com.truward.polymer.domain;

import com.truward.polymer.specification.annotation.SpecificationMethodInvocation;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * Specification service for configuring domain object generation for the given classes.
 *
 * @author Alexander Shabanov
 */
public interface DomainObjectSpecifier {

  /**
   * Identifies provided classes as code generation targets.
   *
   * @param classes Source interface classes
   * @return Current object specifier instance.
   */
  DomainObjectSpecifier targets(@Nonnull Class<?>... classes);

  DomainObjectSpecifier target(@Nonnull Object domainObjectInstance);

  /**
   * Assigns builder to the given domain object instance, previously created by
   * {@link #targets(Class[])} call.
   *
   * @param domainObjectInstance Instance of the domain object.
   */
  @Nonnull
  DomainObjectSpecifier assignBuilder(@Nonnull Object domainObjectInstance);

  @Nonnull
  DomainObjectSpecifier setTargetName(@Nonnull Object domainObjectInstance, @Nonnull FqName targetName);

  /**
   * Instantiates provided class, the instantiated targets should be used then to provide specificator
   * invocations.
   * <code>
   *   final UserAccount account = specifier.domainObject(UserAccount.class);
   *   specifier.isNullable(account.getName()); // specificator invocation
   * </code>
   *
   * @param domainClass Interface class.
   * @param <T> Interface typed.
   * @return Specificator instance.
   */
  @Nonnull
  <T> T domainObject(@Nonnull Class<T> domainClass);

  /**
   * Specifies, that the given field is nullable.
   * By default all the fields are non-nullable.
   *
   * @param invocationResult Getter invocation that triggers information about the corresponding field
   * @return Current specifier, provided for convenience such that the user is able to chain calls.
   */
  @Nonnull
  DomainObjectSpecifier isNullable(@SpecificationMethodInvocation Object invocationResult);

  @Nonnull
  DomainObjectSpecifier isNonNull(@SpecificationMethodInvocation Object invocationResult);

  @Nonnull
  DomainObjectSpecifier hasLength(@SpecificationMethodInvocation String invocationResult);

  @Nonnull
  DomainObjectSpecifier isNonNegative(@SpecificationMethodInvocation int invocationResult);

  @Nonnull
  DomainObjectSettings getObjectSettings(@Nonnull Class<?> clazz);

  @Nonnull
  DomainImplementerSettings getImplementerSettings();
}
