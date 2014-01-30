package com.alexshabanov.sample.specification;

import com.alexshabanov.sample.model.UserAccount;
import com.truward.polymer.annotation.Specification;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.domain.DomainObjectSpecifier;

import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public final class ModelSpecification {
  @Resource
  private DomainObjectSpecifier specifier;

  @Specification
  public void assignBuilder() {
    specifier.getObjectSettings(UserAccount.class).assignBuilder();
  }

  @Specification
  public void domainObject(@DomainObject UserAccount u) {
    specifier.target(UserAccount.class).isNonNull(u.getNickname());
  }
}
