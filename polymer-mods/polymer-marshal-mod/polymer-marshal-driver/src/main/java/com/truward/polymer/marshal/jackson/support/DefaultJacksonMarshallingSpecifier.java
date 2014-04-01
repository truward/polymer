package com.truward.polymer.marshal.jackson.support;

import com.truward.polymer.marshal.json.analysis.JsonMarshallerImplementer;
import com.truward.polymer.marshal.json.support.BaseJsonMarshallingSpecifier;
import com.truward.polymer.marshal.json.JacksonMarshallingSpecifier;

import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public final class DefaultJacksonMarshallingSpecifier extends BaseJsonMarshallingSpecifier implements JacksonMarshallingSpecifier {

  @Resource
  private DefaultJacksonMarshallerImplementer implementer;

  @Override
  protected JsonMarshallerImplementer getImplementer() {
    return implementer;
  }
}
