package com.truward.polymer.core.generator2;

import com.truward.polymer.naming.FqName;

/**
 * @author Alexander Shabanov
 */
public interface CodeGenerator {
  // char
  CodeGenerator c(char ch);

  // string
  CodeGenerator s(String s);

  CodeGenerator s(String... strings);

  CodeGenerator s(FqName fqName);

  // space
  CodeGenerator sp();

  CodeGenerator cl(Class<?> clazz);
}
