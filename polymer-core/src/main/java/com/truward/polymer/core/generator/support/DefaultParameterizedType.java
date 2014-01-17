package com.truward.polymer.core.generator.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Custom support for parameterized types.
 *
 * @author Alexander Shabanov
 */
public class DefaultParameterizedType implements ParameterizedType {
  private final Type owner;
  private final Type rawType;
  private final Type[] arguments;

  public DefaultParameterizedType(Type owner, Type rawType, Type[] arguments) {
    assert rawType != null && arguments != null;
    this.owner = owner;
    this.rawType = rawType;
    this.arguments = Arrays.copyOf(arguments, arguments.length);
  }

  public static ParameterizedType from(Type rawType, Type... args) {
    return new DefaultParameterizedType(null, rawType, args);
  }

  public static ParameterizedType from(Type rawType, List<Type> args) {
    return new DefaultParameterizedType(null, rawType, args.toArray(new Type[args.size()]));
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
