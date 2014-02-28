package com.truward.polymer.core.code.typed;

import com.truward.polymer.core.code.FqNamedObject;

import java.lang.reflect.Type;

/**
 * @author Alexander Shabanov
 */
public interface GenClass extends GenType, FqNamedObject, Type {
  boolean isPrimitive();
}
