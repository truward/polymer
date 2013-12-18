package com.truward.polymer.core.naming;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Alexander Shabanov
 */
public class FqNameTest {

  @Test
  public void shouldParseNestedName() {
    final FqName fqName = FqName.parse("root.child");
    assertNotNull(fqName.getParent());
    assertEquals("root", fqName.getParent().getName());
    assertNull("root", fqName.getParent().getParent());
    assertEquals("child", fqName.getName());
    assertEquals("root.child", fqName.toString());
  }

  @Test
  public void shouldParseRootName() {
    final FqName rootName = FqName.parse("root");
    assertNull(rootName.getParent());
    assertEquals("root", rootName.getName());
    assertEquals("root", rootName.toString());
  }
}
