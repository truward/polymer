package com.truward.polymer.marshal.json.analysis;

import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexander Shabanov
 */
public interface JsonFieldRegistry {

  @Nonnull
  String getJsonName(@Nonnull DomainField domainField);

  @Nullable
  JsonField getField(@Nonnull DomainField domainField);

  void putField(@Nonnull DomainField domainField, @Nonnull JsonField gsonField);
}
