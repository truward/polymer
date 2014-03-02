package com.truward.polymer.testspec.p2;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.testspec.model.User;
import com.truward.polymer.testspec.model.UserRole;

import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public final class UserSpecification {
  @Resource
  private DomainObjectSpecifier specifier;

  @Specification
  public void spec(@DomainObject UserRole userRole) {
    specifier
        .target(UserRole.class)
        .isNonNull(userRole.getName());
  }

  @Specification
  public void spec(@DomainObject User user) {
    specifier
        .target(User.class)
        .isNullable(user.getId())
        .getObjectSettings(User.class).assignBuilder();

    specifier.getObjectSettings(User.class).setTargetName(FqName.parse("com.target.User"));
  }
}
