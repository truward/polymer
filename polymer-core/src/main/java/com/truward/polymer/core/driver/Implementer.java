package com.truward.polymer.core.driver;

import java.io.IOException;

/**
 * Represents an interface to an entity that will take care of code generation
 *
 * @author Alexander Shabanov
 */
public interface Implementer {
  void generateImplementations() throws IOException;
}
