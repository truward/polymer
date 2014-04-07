package com.truward.polymer.code;

/**
 * Holder for AST nodes.
 *
 * @author Alexander Shabanov
 */
public final class Ast {
  /** Hidden */
  private Ast() {}

  /** Base class for all the nodes */
  public static abstract class Node {

  }

  public static abstract class Expr extends Node {}

  public static abstract class Stmt extends Node {}

  public static abstract class TypeExpr extends Node {}

  public static abstract class Modifiers extends Node {

  }

  /**
   * Class, Interface, Enum or Annotation Declaration.
   * @see "JLS 3, sections 8.1, 8.9, 9.1, and 9.6"
   */
  public static abstract class ClassDecl extends Stmt {

  }
}
