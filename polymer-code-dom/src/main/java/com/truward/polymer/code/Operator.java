package com.truward.polymer.code;

import javax.annotation.Nonnull;

/**
 * Represents an unary or binary operator.
 * 
 * @author Alexander Shabanov
 */
public enum Operator {
  BIT_OR("|"),
  BIT_XOR("^"),
  BIT_AND("&"),
  OR("||"),
  EQ("=="),
  AND("&&"),
  POS("+"),
  NEG("-"),
  NOT("!"),
  PRE_INC("++"),
  PRE_DEC("--"),
  POST_INC("++"),
  POST_DEC("--"),
  COMPL("~"),
  NE("!="),
  LT("<"),
  GT(">"),
  LE("<="),
  GE(">="),
  SHL("<<"),
  SHR(">>"),
  USHR(">>>"),
  PLUS("+"),
  MINUS("-"),
  MUL("*"),
  DIV("/"),
  MOD("%");

  private final String value;

  @Nonnull public String getValue() {
    return value;
  }

  private Operator(@Nonnull String value) {
    this.value = value;
  }
}
