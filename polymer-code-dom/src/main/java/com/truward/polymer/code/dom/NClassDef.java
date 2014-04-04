package com.truward.polymer.code.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Class definition
 *
 * @author Alexander Shabanov
 */
public final class NClassDef {
  private NClassHost parent;
  // TODO: parameterized class support?
  private NTypeExpr parentClass;
  private List<NTypeExpr> interfaces = new ArrayList<>();
  private List<NStmt> members = new ArrayList<>();
}
