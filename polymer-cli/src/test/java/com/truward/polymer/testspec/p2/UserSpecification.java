package com.truward.polymer.testspec.p2;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.domain.DomainObjectSpecifier;
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
        .isNonNull(userRole.getName());
  }

  @Specification
  public void spec(@DomainObject User user) {
    specifier
        .isNonNull(user.getName());

    specifier.getSettingsFor(User.class).setImplementationName("User");
  }
}
