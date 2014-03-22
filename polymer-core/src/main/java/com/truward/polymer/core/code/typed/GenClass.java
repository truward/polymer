package com.truward.polymer.core.code.typed;

import com.truward.polymer.core.code.FqNamedObject;

/**
 * @author Alexander Shabanov
 */
public interface GenClass extends GenType, FqNamedObject {
  boolean isPrimitive();
}
