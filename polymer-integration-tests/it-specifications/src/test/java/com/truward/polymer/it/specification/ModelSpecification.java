package com.truward.polymer.it.specification;

import com.truward.polymer.specification.annotation.Specification;
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

  private final FqName targetPackage = FqName.valueOf("com.truward.polymer.generated.model");

  @Specification
  public void globalSettings() {
    specifier.getImplementerSettings().setTargetPackageName(targetPackage);
  }

  @Specification
  public void userModel(@DomainObject User user) {
    specifier
        .target(user)
        .assignBuilder(user)
        .isNonNull(user.getName())
        .isNullable(user.getId())
        .isNonNegative(user.getAge())
    ;
  }
}
