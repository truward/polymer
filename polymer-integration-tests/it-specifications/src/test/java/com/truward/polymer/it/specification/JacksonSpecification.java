package com.truward.polymer.it.specification;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.it.model.User;
import com.truward.polymer.marshal.json.JacksonMarshallingSpecifier;
import com.truward.polymer.naming.FqName;

import javax.annotation.Resource;

/**
 * Jackson serializer specification
 *
 * @author Alexander Shabanov
 */
public final class JacksonSpecification {

  @Resource
  private JacksonMarshallingSpecifier jacksonMarshallerSpecifier;

  @Specification
  public void jacksonSpecification() {
    jacksonMarshallerSpecifier.addDomainEntity(User.class);
    jacksonMarshallerSpecifier.setTargetClassName(FqName.valueOf("com.truward.polymer.generated.jackson.JacksonMarshallers"));
  }
}
