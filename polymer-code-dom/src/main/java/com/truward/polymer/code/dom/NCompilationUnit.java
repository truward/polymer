package com.truward.polymer.code.dom;

import com.truward.polymer.naming.FqName;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class NCompilationUnit implements NClassHost {
  private NClassDef classDef;
  // filled on the analysis stage
  private Map<String, FqName> imports = new LinkedHashMap<>();
}
