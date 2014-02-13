package com.truward.polymer.marshal.gson.specification.support;

import com.truward.polymer.marshal.gson.GsonMarshallingSpecifier;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public final class DefaultGsonMarshallingSpecifier implements GsonMarshallingSpecifier {
  @Override
  public GsonMarshallingSpecifier setGeneratorTarget(@Nonnull FqName targetMethod) {
    throw new UnsupportedOperationException();
  }

  @Override
  public GsonMarshallingSpecifier addDomainEntity(@Nonnull Class<?> entityClass) {
    throw new UnsupportedOperationException();
  }
}
