package com.truward.polymer.specification.domain;

import com.truward.polymer.specification.annotation.SpecificationMethodInvocation;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface DomainBehaviorSpecifier {

  @Nonnull
  DomainBehaviorSpecifier isNullable(@SpecificationMethodInvocation Object r);

  @Nonnull
  DomainBehaviorSpecifier isNonNull(@SpecificationMethodInvocation Object r);

  @Nonnull
  DomainBehaviorSpecifier hasLength(@SpecificationMethodInvocation String r);

  @Nonnull
  DomainBehaviorSpecifier isNonNegative(@SpecificationMethodInvocation int r);

  @Nonnull
  DomainBehaviorSpecifier isNonNegative(@SpecificationMethodInvocation long r);

  @Nonnull
  DomainBehaviorSpecifier isNonNegative(@SpecificationMethodInvocation double r);

  @Nonnull
  DomainBehaviorSpecifier isInRange(@SpecificationMethodInvocation int r, int from, int to);

  @Nonnull
  DomainBehaviorSpecifier isInRange(@SpecificationMethodInvocation long r, long from, long to);

  @Nonnull
  DomainBehaviorSpecifier isInRange(@SpecificationMethodInvocation double r, double from, double to);
}
