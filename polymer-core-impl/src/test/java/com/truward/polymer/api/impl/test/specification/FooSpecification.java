package com.truward.polymer.api.impl.test.specification;

import com.truward.polymer.api.impl.test.plugin.foo.FooArgument;
import com.truward.polymer.specification.annotation.Specification;

/**
 * @author Alexander Shabanov
 */
public final class FooSpecification {
  public int fooArg;

  @Specification
  public void foo1(@FooArgument int arg) {
    fooArg += arg;
  }

  @Specification
  public void foo2(@FooArgument int arg) {
    fooArg += arg;
  }
}
