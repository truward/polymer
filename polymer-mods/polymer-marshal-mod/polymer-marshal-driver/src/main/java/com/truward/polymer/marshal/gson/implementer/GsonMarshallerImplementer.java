package com.truward.polymer.marshal.gson.implementer;

import com.truward.polymer.marshal.gson.analysis.GsonTarget;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface GsonMarshallerImplementer {
  void submit(@Nonnull GsonTarget gsonTarget);
}
