package com.truward.polymer.core.code.builder;

import com.truward.polymer.core.code.typed.GenType;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface TypeManager {

  void start(@Nonnull FqName currentPackage);

  @Nonnull
  GenType adaptType(@Nonnull Type type);

  @Nonnull
  List<FqName> getImportNames();
}
