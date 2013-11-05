package com.truward.polymer.core.typesystem.model;

/**
 * @author Alexander Shabanov
 */
public abstract class TypeExprVisitor {
  public void visitTypeExpr(TypeExpr node) {
    throw new UnsupportedOperationException("Unsupported node for this visitor: " + node);
  }

  public void visitClassRef(ClassRef node) {
    visitTypeExpr(node);
  }

  public void visitGenericExpr(GenericExpr node) {
    visitTypeExpr(node);
  }

  public void visitPrimitiveType(PrimitiveType node) {
    visitTypeExpr(node);
  }

  public void visitArrayType(ArrayType node) {
    visitTypeExpr(node);
  }
}
