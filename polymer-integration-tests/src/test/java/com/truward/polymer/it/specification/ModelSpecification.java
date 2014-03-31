package com.truward.polymer.it.specification;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.it.model.User;
import com.truward.polymer.naming.FqName;

import javax.annotation.Resource;

/**
 * Domain object specification
 */
public final class ModelSpecification {
  @Resource
  private DomainObjectSpecifier specifier;

  private final FqName targetPackage = FqName.parse("com.truward.polymer.generated.model");

  @Specification
  public void domainObjects() {
    specifier.target(User.class);
    specifier.getImplementerSettings().setDefaultTargetPackageName(targetPackage);
  }

  @Specification
  public void userModel(@DomainObject User user) {
    specifier.isNonNull(user.getName());
    specifier.isNullable(user.getId());
    specifier.isNonNegative(user.getAge());
    specifier.getObjectSettings(User.class).assignBuilder();
  }
}
