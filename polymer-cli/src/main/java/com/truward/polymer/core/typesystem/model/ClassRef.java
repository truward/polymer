package com.truward.polymer.core.typesystem.model;

/**
 * @author Alexander Shabanov
 */
public final class ClassRef implements TypeExpr, TypeParameter {
  private final Class<?> associatedClass;

  public ClassRef(Class<?> associatedClass) {
    if (associatedClass == null) {
      throw new IllegalArgumentException("associatedClass is null");
    }
    this.associatedClass = associatedClass;
  }

  public Class<?> getAssociatedClass() {
    return associatedClass;
  }

  @Override
  public void apply(TypeExprVisitor visitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ClassRef) && ((ClassRef) o).getAssociatedClass().equals(getAssociatedClass());
  }

  @Override
  public int hashCode() {
    return associatedClass.hashCode();
  }

  @Override
  public String toString() {
    return associatedClass.getName();
  }
}
