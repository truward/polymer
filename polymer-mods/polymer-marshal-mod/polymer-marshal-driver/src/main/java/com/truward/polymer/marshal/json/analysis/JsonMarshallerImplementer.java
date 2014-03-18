package com.truward.polymer.marshal.json.analysis;

import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.domain.analysis.support.GenDomainClass;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface JsonMarshallerImplementer extends Freezable, Implementer {
  void submit(@Nonnull GenDomainClass domainClass);
}
