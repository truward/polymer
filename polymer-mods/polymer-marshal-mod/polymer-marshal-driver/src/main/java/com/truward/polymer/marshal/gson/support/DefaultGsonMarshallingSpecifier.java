package com.truward.polymer.marshal.gson.support;

import com.truward.polymer.marshal.jackson.support.DefaultJacksonMarshallerImplementer;
import com.truward.polymer.marshal.json.GsonMarshallingSpecifier;
import com.truward.polymer.marshal.json.analysis.JsonMarshallerImplementer;
import com.truward.polymer.marshal.json.support.BaseJsonMarshallingSpecifier;

import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public final class DefaultGsonMarshallingSpecifier extends BaseJsonMarshallingSpecifier implements GsonMarshallingSpecifier {
  @Resource
  private DefaultGsonMarshallerImplementer implementer;

  @Override
  protected JsonMarshallerImplementer getImplementer() {
    return implementer;
  }
}
