package com.truward.polymer.core.driver;

/**
 * Handler of the specification classes, that maintains inner structure of the domain class.
 *
 * @author Alexander Shabanov
 */
public interface SpecificationHandler {

  boolean parseClass(Class<?> clazz);
}
