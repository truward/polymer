package com.truward.polymer.marshal.json;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface JsonMarshallingSpecifier {

  JsonMarshallingSpecifier setGeneratorTarget(@Nonnull FqName targetClass);

  JsonMarshallingSpecifier addDomainEntity(@Nonnull Class<?> entityClass);
}
