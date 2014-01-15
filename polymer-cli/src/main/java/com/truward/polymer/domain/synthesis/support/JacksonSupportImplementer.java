package com.truward.polymer.domain.synthesis.support;

import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

/**
 * @author Alexander Shabanov
 */
public final class JacksonSupportImplementer {
  private Class<?> jacksonPropertyAnnotation;
  private final JavaCodeGenerator generator;
  private final Type implClass;
  private final DomainAnalysisResult analysisResult;

  public JacksonSupportImplementer(@Nonnull JavaCodeGenerator generator,
                                   @Nonnull Type implClass,
                                   @Nonnull DomainAnalysisResult analysisResult) {
    this.generator = generator;
    this.implClass = implClass;
    this.analysisResult = analysisResult;
  }


  public boolean isInnerCreatorSupported() {
    return false;  // TODO: impl
  }

  public void generateInnerCreator() {
    // TODO: impl
  }
}
