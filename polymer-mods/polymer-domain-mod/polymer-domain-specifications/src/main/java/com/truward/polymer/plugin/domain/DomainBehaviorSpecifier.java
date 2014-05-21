package com.truward.polymer.plugin.domain;

import com.truward.polymer.annotation.SpecificationMethodInvocation;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.Collection;

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

    @Nonnull
    DomainBehaviorSpecifier addCustomCheck(@SpecificationMethodInvocation Object r,
                                           @Nonnull FqName staticMethodName,
                                           @Nonnull Collection<Type> arguments);

    @Nonnull
    DomainBehaviorSpecifier withCustomGetter(@SpecificationMethodInvocation Object r,
                                             @Nonnull FqName staticMethodName,
                                             @Nonnull Collection<Type> arguments);

    @Nonnull
    DomainBehaviorSpecifier withCustomSetter(@SpecificationMethodInvocation Object r,
                                             @Nonnull FqName staticMethodName,
                                             @Nonnull Collection<Type> arguments);
}
