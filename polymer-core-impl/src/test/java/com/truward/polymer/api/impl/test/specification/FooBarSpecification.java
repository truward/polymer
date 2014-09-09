package com.truward.polymer.api.impl.test.specification;

import com.truward.polymer.api.impl.test.plugin.bar.BarArgument;
import com.truward.polymer.api.impl.test.plugin.foo.FooArgument;
import com.truward.polymer.specification.annotation.Specification;

/**
 * @author Alexander Shabanov
 */
public class FooBarSpecification {
  public int fooArg;
  public String barArg = "";
  public int orderControl;

  @Specification(ordinal = 1)
  public void foo1(@FooArgument int arg) {
    this.fooArg = arg;
    this.orderControl = orderControl * 10 + 1;
  }

  @Specification(ordinal = 3)
  public void foo3(@FooArgument int arg) {
    this.fooArg += arg;
    this.orderControl = orderControl * 10 + 3;
  }

  @Specification(ordinal = 5)
  public void foo5(@FooArgument int arg) {
    this.fooArg += arg;
    this.orderControl = orderControl * 10 + 5;
  }

  @Specification(ordinal = 4)
  public void foo4(@FooArgument int arg, @BarArgument String barArg) {
    this.fooArg += arg;
    this.barArg += barArg;
    this.orderControl = orderControl * 10 + 4;
  }

  @Specification(ordinal = 6)
  public void bar1(@BarArgument String barArg) {
    this.barArg += barArg;
    this.orderControl = orderControl * 10 + 6;
  }

  @Specification(ordinal = 2)
  public void foo2(@FooArgument int arg) {
    this.fooArg += arg;
    this.orderControl = orderControl * 10 + 2;
  }

  @Specification(ordinal = 7)
  public void bar2(@BarArgument String barArg) {
    this.barArg += barArg;
    this.orderControl = orderControl * 10 + 7;
  }
}
