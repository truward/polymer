package com.truward.polymer.builder;

/**
 * @author Alexander Shabanov
 */
public interface BuilderSupport<T, B extends GenericBuilder<T>> {
  B newBuilder();

  B newBuilder(T object);
}
