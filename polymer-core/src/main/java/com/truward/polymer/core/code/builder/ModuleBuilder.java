package com.truward.polymer.core.code.builder;

import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.naming.FqName;

import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface ModuleBuilder extends Freezable {
  void start(FqName packageName);

  void insertImports(List<FqName> imports);

  CodeStream getStream();

  FqName getPackageName();
}
