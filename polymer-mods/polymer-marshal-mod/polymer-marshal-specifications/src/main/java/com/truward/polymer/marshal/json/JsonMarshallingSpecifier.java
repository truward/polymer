package com.truward.polymer.marshal.json;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface JsonMarshallingSpecifier {

  @Nonnull
  JsonMarshallingSpecifier setTargetClassName(@Nonnull FqName targetClassName);

  @Nonnull
  JsonMarshallingSpecifier addDomainEntity(@Nonnull Class<?> entityClass);
}
