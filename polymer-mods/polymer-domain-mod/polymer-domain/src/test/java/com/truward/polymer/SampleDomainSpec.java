package com.truward.polymer;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.domain.DomainObjectSpecifier;

import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public class SampleDomainSpec {
  interface User {
    String getName();
    int getAge();
  }

  @Resource
  private DomainObjectSpecifier specifier;

  @Specification
  public void user() {
    final User user = specifier.domainObject(User.class);
    specifier
        .hasLength(user.getName())
        .isNonNegative(user.getAge());
  }
}
