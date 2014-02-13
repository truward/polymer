package com.truward.polymer.marshal.gson;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface GsonMarshallingSpecifier {

  GsonMarshallingSpecifier setGeneratorTarget(@Nonnull FqName targetMethod);

  GsonMarshallingSpecifier addDomainEntity(@Nonnull Class<?> entityClass);
}
