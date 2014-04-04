package com.truward.polymer.code.dom;

import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class NPackage implements Node {
  private NPackage parent;
  private Map<String, NCompilationUnit> compilationUnits;

  public NPackage(NPackage parent) {
    this.parent = parent;
  }


}
