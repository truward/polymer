package com.truward.polymer.core.typesystem.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class GenericExpr implements TypeExpr {
  private final TypeExpr type;
  private final List<TypeParameter> parameters;

  public GenericExpr(TypeExpr type, List<TypeParameter> parameters) {
    if (type == null) {
      throw new IllegalArgumentException("type");
    }
    if (parameters == null || parameters.size() < 0) {
      throw new IllegalArgumentException("parameters");
    }

    this.type = type;
    this.parameters = ImmutableList.copyOf(parameters);
  }

  public TypeExpr getType() {
    return type;
  }

  public List<TypeParameter> getParameters() {
    return parameters;
  }

  @Override
  public void apply(TypeExprVisitor visitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof GenericExpr) {
      final GenericExpr that = (GenericExpr) o;
      return that.getType().equals(getType()) && that.getParameters().equals(getParameters());
    }

    return false;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + parameters.hashCode();
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(getType());
    builder.append('<');
    boolean next = false;
    for (final TypeParameter parameter : getParameters()) {
      if (next) {
        builder.append(", ");
      } else {
        next = true;
      }
      builder.append(parameter);
    }
    builder.append('>');
    return builder.toString();
  }
}
