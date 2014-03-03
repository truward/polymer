package com.truward.polymer.marshal.gson.analysis;

import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.gson.analysis.GsonTarget;
import com.truward.polymer.marshal.gson.support.analysis.DefaultGsonTarget;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface GsonMarshallerImplementer extends Freezable, Implementer {
  void submit(@Nonnull GenDomainClass domainClass);
}
