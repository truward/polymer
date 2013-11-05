package com.truward.polymer.core.typesystem.model;

/**
 * @author Alexander Shabanov
 */
public enum PrimitiveType implements TypeExpr {
  BYTE("byte"),
  SHORT("short"),
  CHAR("char"),
  INT("int"),
  LONG("long"),
  FLOAT("float"),
  DOUBLE("double"),
  VOID("void");

  private final String name;

  public String getName() {
    return name;
  }

  PrimitiveType(String name) {
    this.name = name;
  }

  @Override
  public void apply(TypeExprVisitor visitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return getName();
  }
}
