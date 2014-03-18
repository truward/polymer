package com.truward.polymer.marshal.jackson.support;

import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.json.analysis.JsonMarshallerImplementer;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public class DefaultJacksonMarshallerImplementer extends FreezableSupport
    implements JsonMarshallerImplementer, SpecificationStateAware {
  @Override
  public void submit(@Nonnull GenDomainClass domainClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void generateImplementations() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    throw new UnsupportedOperationException();
  }
}
