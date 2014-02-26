package com.truward.polymer.core.code.builder;

import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.naming.FqName;

import java.lang.reflect.Type;

/**
 * Represents code output stream
 *
 * @author Alexander Shabanov
 */
public interface CodeStream extends Freezable {

  // char
  ModuleBuilder c(char ch);

  ModuleBuilder c(char... chars);

  // string
  ModuleBuilder s(String string);

  ModuleBuilder s(String... strings);

  ModuleBuilder s(FqName fqName);

  // space
  ModuleBuilder sp();

  // types
  ModuleBuilder t(Type type);
}
