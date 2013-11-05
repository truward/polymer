package com.truward.polymer.core.typesystem.model;

/**
 * Represents type expression
 *
 * @author Alexander Shabanov
 */
public interface TypeExpr {
  void apply(TypeExprVisitor visitor);
}
