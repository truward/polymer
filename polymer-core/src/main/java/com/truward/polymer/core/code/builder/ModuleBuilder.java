package com.truward.polymer.core.code.builder;

import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.freezable.Freezable;

/**
 * @author Alexander Shabanov
 */
public interface ModuleBuilder extends Freezable {

  GenInlineBlock getStream();
}
