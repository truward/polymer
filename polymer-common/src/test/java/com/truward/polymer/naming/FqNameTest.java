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
    final FqName fqName = FqName.valueOf("root.child");
    assertEquals("root", fqName.getParent().getName());
    assertTrue(fqName.getParent().isRoot());
    assertEquals("child", fqName.getName());
    assertEquals("root.child", fqName.toString());
  }

  @Test
  public void shouldParseRootName() {
    final FqName rootName = FqName.valueOf("root");
    assertTrue(rootName.isRoot());
    assertEquals("root", rootName.getName());
    assertEquals("root", rootName.toString());
  }

  @Test(expected = IllegalStateException.class)
  public void shouldDisallowAccessToParentOfRoot() {
    FqName.valueOf("root").getParent();
  }

  @Test
  public void shouldLongNameBeEqualToItsEquivalent() {
    final String name = "com.mysite.demo.sample.MyClass";
    final FqName fqName1 = FqName.valueOf(name);
    final FqName fqName2 = FqName.valueOf(new String(name.toCharArray()));
    final FqName fqName3 = FqName.valueOf(fqName1.toString());

    // equals test
    assertEquals(fqName1, fqName2);
    assertEquals(fqName2, fqName3);
    assertEquals(fqName3, fqName1);

    // hashCode test
    assertEquals(fqName1.hashCode(), fqName2.hashCode());
    assertEquals(fqName2.hashCode(), fqName3.hashCode());
    assertEquals(fqName3.hashCode(), fqName1.hashCode());
  }

  @Test
  public void shouldBeEqualAndHaveSameHashCode() {
    final FqName foo = FqName.valueOf("Foo");
    final FqName[][] nameArrs = {
        { foo, foo, FqName.valueOf("Foo") },
        { FqName.valueOf("com.Foo"), FqName.valueOf("com.Foo"), new FqName("Foo", new FqName("com", null)) },
        { FqName.valueOf("com.mysite.Foo"), FqName.valueOf("com.mysite.Foo"),
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
    final FqName name = FqName.valueOf("com.mysite.Foo");
    final StringBuilder builder = new StringBuilder();
    name.appendTo(builder, '/');
    assertEquals("com/mysite/Foo", builder.toString());
  }

  @Test
  public void shouldJoinNames() {
    assertEquals("com.mysite.domain", FqName.valueOf("com.mysite").append(FqName.valueOf("domain")).toString());
    assertEquals("com.mysite.domain", FqName.valueOf("com").append(FqName.valueOf("mysite.domain")).toString());
  }

  @Test
  public void shouldConvertToList() {
    assertEquals(Arrays.asList("com"), FqName.valueOf("com").toList());
    assertEquals(Arrays.asList("com", "mysite"), FqName.valueOf("com.mysite").toList());
  }

  @Test
  public void shouldNotBeEqual() {
    final FqName[] names = {
        FqName.valueOf("com.mysite.Foo"),
        FqName.valueOf("Foo"),
        FqName.valueOf("com.mysite.service.Foo"),
        FqName.valueOf("org.mysite.Foo"),
        FqName.valueOf("com.anothersite.Foo"),
        FqName.valueOf("org.mysite"),
        FqName.valueOf("com.mysite.Bar")
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
          final FqName clone = FqName.valueOf(name.toString());
          assertEquals(0, name.compareTo(name));
          assertEquals(0, name.compareTo(clone));
          assertEquals(name, clone);
          assertEquals(clone, name);
          assertEquals(name.hashCode(), clone.hashCode());
        }
      }
    }
  }

  @Test
  public void shouldAppend() {
    final FqName com = FqName.valueOf("com");
    assertEquals(FqName.valueOf("com.mysite"), com.append("mysite"));
    assertEquals(FqName.valueOf("com.mysite.demo"), com.append("mysite").append("demo"));
  }

  @Test
  public void shouldCalculateCount() {
    assertEquals(1, FqName.valueOf("com").count());
    assertEquals(2, FqName.valueOf("com.mysite").count());
    assertEquals(3, FqName.valueOf("com.mysite.demo").count());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIAEOnEmptyString() {
    FqName.valueOf("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIAEOnMalformedName() {
    FqName.valueOf("com..mysite");
  }
}
