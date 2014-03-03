package com.truward.polymer.marshal.gson.analysis;

import com.truward.polymer.domain.analysis.DomainField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexander Shabanov
 */
public interface GsonFieldRegistry {

  @Nullable
  GsonField getField(@Nonnull DomainField domainField);

  void putField(@Nonnull DomainField domainField, @Nonnull GsonField gsonField);

  @Nonnull
  GsonField adapt(@Nonnull DomainField domainField);
}
