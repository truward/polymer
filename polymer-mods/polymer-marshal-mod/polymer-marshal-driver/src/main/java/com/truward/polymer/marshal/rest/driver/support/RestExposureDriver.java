package com.truward.polymer.marshal.rest.driver.support;

import com.truward.polymer.core.driver.SpecificationParameterProvider;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.marshal.Exposed;
import com.truward.polymer.marshal.rest.RestSpecifier;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public class RestExposureDriver implements SpecificationParameterProvider, SpecificationStateAware {


  @Override
  public <T extends Annotation, R> boolean canProvideParameter(@Nonnull List<T> annotations, @Nonnull Class<R> resultType) {
    if (annotations.size() == 1 && Exposed.class.isAssignableFrom(annotations.get(0).getClass())) {
      return true; // exposed service object
    } else if (annotations.size() == 0 && resultType.equals(RestSpecifier.class)) {
      return true; // associated rest service
    }

    return false;
  }

  @Override
  public <T extends Annotation, R> R provideParameter(@Nonnull List<T> annotations, @Nonnull Class<R> resultType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    throw new UnsupportedOperationException();
  }
}
