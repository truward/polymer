package com.truward.polymer.api.plugin;

import java.io.IOException;

/**
 * Interface to the plugin part, which takes care about generating implementations by using the
 * previously processed specifications.
 *
 * @author Alexander Shabanov
 */
public interface SpecificationImplementer {

  /**
   * Generates the corresponding implementation.
   *
   * @throws IOException On I/O error
   */
  void generateImplementations() throws IOException;
}
