package com.truward.polymer.testspec.p1;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.domain.DomainObjectSpecifier;

import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public class UserSpecification {
  @Resource
  private DomainObjectSpecifier specifier;

  @Specification
  public void spec() {

  }
}
