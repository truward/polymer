package com.truward.polymer.freezable;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests freezable support
 *
 * @author Alexander Shabanov
 */
public final class FreezableSupportTest {
  private Foo foo;
  private final int bar = 1; // valid bar value

  @Before
  public void init() {
    foo = new FooImpl();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldCheckFreezeState() {
    foo.getBar();
  }

  @Test(expected = CannotBeFrozenException.class)
  public void shouldCheckFreezeStateOnFreeze() {
    foo.freeze();
  }

  @Test
  public void shouldInitFreezeAndGet() {
    foo.setBar(bar);
    foo.freeze();
    assertEquals(bar, foo.getBar());
  }

  @Test
  public void shouldFreezeTwice() {
    foo.setBar(bar);
    foo.freeze();
    foo.freeze();
  }

  @Test(expected = IllegalStateException.class)
  public void shouldRejectModificationAfterFreeze() {
    foo.setBar(bar);
    foo.freeze();
    foo.setBar(bar);
  }

  //
  // Sample class
  //

  public interface Foo extends Freezable {
    int getBar();
    void setBar(int bar);
  }

  public static final class FooImpl extends FreezableSupport implements Foo {
    int bar;

    @Override public int getBar() {
      checkIsFrozen();
      return bar;
    }

    @Override public void setBar(int bar) {
      checkNonFrozen();
      this.bar = bar;
    }

    @Override
    protected void beforeFreezing() {
      cannotBeFrozenIf(bar <= 0, "bar value should be positive");
      super.beforeFreezing();
    }
  }
}
