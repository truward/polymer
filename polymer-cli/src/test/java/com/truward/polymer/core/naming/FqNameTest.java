package com.truward.polymer.core.naming;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Alexander Shabanov
 */
public final class FqNameTest {

  @Test
  public void shouldParseNestedName() {
    final FqName fqName = FqName.parse("root.child");
    assertEquals("root", fqName.getParent().getName());
    assertTrue(fqName.getParent().isRoot());
    assertEquals("child", fqName.getName());
    assertEquals("root.child", fqName.toString());
  }

  @Test
  public void shouldParseRootName() {
    final FqName rootName = FqName.parse("root");
    assertTrue(rootName.isRoot());
    assertEquals("root", rootName.getName());
    assertEquals("root", rootName.toString());
  }

  @Test(expected = IllegalStateException.class)
  public void shouldDisallowAccessToParentOfRoot() {
    FqName.parse("root").getParent();
  }
}
