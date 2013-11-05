package com.truward.polymer.domain.analysis;

/**
 * @author Alexander Shabanov
 */
public class DomainAnalysisContext {

  public DomainAnalysisResult analyze(Class<?> clazz) {
    if (!clazz.isInterface()) {
      throw new IllegalArgumentException("Only interfaces are expected, got " + clazz);
    }

    return new DomainAnalysisResult(clazz);
  }
}
