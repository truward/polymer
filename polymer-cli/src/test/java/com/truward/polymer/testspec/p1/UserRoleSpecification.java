package com.truward.polymer.testspec.p1;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.testspec.model.UserRole;

import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public class UserRoleSpecification {
  @Resource
  private DomainObjectSpecifier specifier;

  @Specification
  public void spec(@DomainObject UserRole userRole) {
    specifier
        .target(userRole)
        .isNullable(userRole.getDescription())
        .hasLength(userRole.getName());
  }
}
