package com.truward.polymer.core.typesystem.model;

/**
 * @author Alexander Shabanov
 */
public final class ArrayType implements TypeExpr {
  private final TypeExpr elementType;
  private final int dimension;

  public ArrayType(TypeExpr elementType, int dimension) {
    if (elementType == null) {
      throw new IllegalArgumentException("elementType is null");
    }
    if (dimension <= 0) {
      throw new IllegalArgumentException("dimension is invalid");
    }
    this.elementType = elementType;
    this.dimension = dimension;
  }

  public TypeExpr getElementType() {
    return elementType;
  }

  public int getDimension() {
    return dimension;
  }

  @Override
  public void apply(TypeExprVisitor visitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final ArrayType arrayType = (ArrayType) o;

    return getDimension() == arrayType.getDimension() && getElementType().equals(arrayType.getElementType());
  }

  @Override
  public int hashCode() {
    int result = elementType.hashCode();
    result = 31 * result + dimension;
    return result;
  }

  @Override
  public String toString() {
    final String elementStr = elementType.toString();
    final StringBuilder builder = new StringBuilder(elementStr.length() + 2 * dimension);
    builder.append(elementStr);
    return builder.toString();
  }
}
