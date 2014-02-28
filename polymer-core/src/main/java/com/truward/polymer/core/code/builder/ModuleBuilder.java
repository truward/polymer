package com.truward.polymer.core.code.builder;

import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.naming.FqName;

import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface ModuleBuilder extends Freezable {

  GenInlineBlock getStream();
}
