package com.truward.polymer.core.support.code;

import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenType;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents a stub type manager that throws error on each invocation.
 *
 * @author Alexander Shabanov
 */
public final class StubTypeManager implements TypeManager {

  public static final StubTypeManager INSTANCE = new StubTypeManager();

  private StubTypeManager() {
  }

  @Override
  public void start() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setPackageName(@Nonnull FqName currentPackage) {
    throw new UnsupportedOperationException();
  }

  @Nonnull
  @Override
  public GenType adaptType(@Nonnull Type type) {
    throw new UnsupportedOperationException();
  }

  @Nonnull
  @Override
  public List<FqName> getImportNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isFqNameRequired(@Nonnull GenClass genClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void freeze() {
    throw new UnsupportedOperationException();
  }
}
