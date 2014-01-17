package com.truward.polymer.core.driver;

/**
 * Handler of the specification classes, that maintains inner structure of the domain class.
 *
 * @author Alexander Shabanov
 */
public interface SpecificationHandler {

  void parseClass(Class<?> clazz);

  void done();
}
