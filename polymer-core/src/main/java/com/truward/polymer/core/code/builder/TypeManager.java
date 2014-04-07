package com.truward.polymer.core.code.builder;

import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenType;
import com.truward.polymer.freezable.Freezable;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface TypeManager extends Freezable {

  void start();

  void setPackageName(@Nonnull FqName currentPackage);

  @Nonnull
  GenType adaptType(@Nonnull Type type);

  @Nonnull
  List<FqName> getImportNames();

  boolean isFqNameRequired(@Nonnull GenClass genClass);
}
