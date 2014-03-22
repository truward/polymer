package com.truward.polymer.core.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Custom support for parameterized types.
 *
 * @author Alexander Shabanov
 */
public final class SynteticParameterizedType implements ParameterizedType {
  private final Type owner;
  private final Type rawType;
  private final Type[] arguments;

  public SynteticParameterizedType(Type owner, Type rawType, Type[] arguments) {
    if (rawType == null) {
      throw new IllegalArgumentException("rawType");
    }
    if (arguments == null) {
      throw new IllegalArgumentException("arguments");
    }
    this.owner = owner;
    this.rawType = rawType;
    this.arguments = Arrays.copyOf(arguments, arguments.length);
  }

  public static ParameterizedType from(Type rawType, Type... args) {
    return new SynteticParameterizedType(null, rawType, args);
  }

  public static ParameterizedType from(Type rawType, List<? extends Type> args) {
    return new SynteticParameterizedType(null, rawType, args.toArray(new Type[args.size()]));
  }

  @Override
  public Type[] getActualTypeArguments() {
    return Arrays.copyOf(arguments, arguments.length);
  }

  @Override
  public Type getRawType() {
    return rawType;
  }

  @Override
  public Type getOwnerType() {
    return owner;
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder(250);
    result.append(rawType).append('<');
    for (int i = 0; i < arguments.length; ++i) {
      if (i > 0) {
        result.append(", ");
      }
      result.append(arguments[i]);
    }
    result.append('>');
    return result.toString();
  }
}
