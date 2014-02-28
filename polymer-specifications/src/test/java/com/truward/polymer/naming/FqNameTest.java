package com.truward.polymer.naming;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

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

  @Test
  public void shouldBeEqualAndHaveSameHashCode() {
    final FqName foo = FqName.parse("Foo");
    final FqName[][] nameArrs = {
        { foo, foo, FqName.parse("Foo") },
        { FqName.parse("com.Foo"), FqName.parse("com.Foo"), new FqName("Foo", new FqName("com", null)) },
        { FqName.parse("com.mysite.Foo"), FqName.parse("com.mysite.Foo"),
            new FqName("Foo", new FqName("mysite", new FqName("com", null))) }
    };

    for (FqName[] names : nameArrs) {
      FqName first = null;
      for (final FqName name : names) {
        if (first == null) {
          first = name;
        } else {
          assertEquals("Name " + name + " should match its copy", first, name);
          assertEquals("Hash code of " + name + " should match its copy", first.hashCode(), name.hashCode());
        }
      }
    }
  }

  @Test
  public void shouldAppendWithCustomSeparator() throws IOException {
    final FqName name = FqName.parse("com.mysite.Foo");
    final StringBuilder builder = new StringBuilder();
    name.appendTo(builder, '/');
    assertEquals("com/mysite/Foo", builder.toString());
  }

  @Test
  public void shouldJoinNames() {
    assertEquals("com.mysite.domain", FqName.parse("com.mysite").join(FqName.parse("domain")).toString());
    assertEquals("com.mysite.domain", FqName.parse("com").join(FqName.parse("mysite.domain")).toString());
  }

  @Test
  public void shouldConvertToList() {
    assertEquals(Arrays.asList("com"), FqName.parse("com").toList());
    assertEquals(Arrays.asList("com", "mysite"), FqName.parse("com.mysite").toList());
  }

  @Test
  public void shouldNotBeEqual() {
    final FqName[] names = {
        FqName.parse("com.mysite.Foo"),
        FqName.parse("Foo"),
        FqName.parse("com.mysite.service.Foo"),
        FqName.parse("org.mysite.Foo"),
        FqName.parse("com.anothersite.Foo"),
        FqName.parse("org.mysite"),
        FqName.parse("com.mysite.Bar")
    };

    for (int i = 0; i < names.length; ++i) {
      for (int j = 0; j < names.length; ++j) {
        if (i != j) {
          assertNotSame("Names should not be the same", names[i], names[j]);

          // compareTo tests
          final int cmp = names[i].compareTo(names[j]);
          assertNotSame(0, cmp);
          assertEquals(cmp, -names[j].compareTo(names[i]));

          // compare hash codes
          final int hc1 = names[i].hashCode();
          final int hc2 = names[j].hashCode();

          // Hash codes might be the same, but it's highly unlikely for them to match for our tested strings
          // It is not necessarily an error if they do match each other, but
          // it might indicate a hash code implementation drawback.
          if (hc1 == hc2) {
            System.err.println("Hash codes of names=[" + names[i] + ", " + names[j] + "] matches");
          }
        } else {
          // assert matches
          final FqName name = names[i];
          final FqName clone = FqName.parse(name.toString());
          assertEquals(0, name.compareTo(name));
          assertEquals(0, name.compareTo(clone));
          assertEquals(name, clone);
          assertEquals(clone, name);
          assertEquals(name.hashCode(), clone.hashCode());
        }
      }
    }
  }
}
