package com.truward.polymer.plugin.domain;

import com.truward.polymer.annotation.SpecificationMethodInvocation;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * TODO: move to the separate plugin
 *
 * @author Alexander Shabanov
 */
public interface InjectedBehaviorSpecifier {

  @Nonnull
  InjectedBehaviorSpecifier addCheck(@SpecificationMethodInvocation Object r,
                                     @Nonnull FqName staticMethodName,
                                     @Nonnull Collection<Type> arguments);

  @Nonnull
  InjectedBehaviorSpecifier bindGetter(@SpecificationMethodInvocation Object r,
                                       @Nonnull FqName staticMethodName,
                                       @Nonnull Collection<Type> arguments);

  @Nonnull
  InjectedBehaviorSpecifier bindSetter(@SpecificationMethodInvocation Object r,
                                       @Nonnull FqName staticMethodName,
                                       @Nonnull Collection<Type> arguments);
}
