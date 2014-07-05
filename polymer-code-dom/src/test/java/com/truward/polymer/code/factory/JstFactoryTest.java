package com.truward.polymer.code.factory;

import com.truward.polymer.code.Jst;
import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link com.truward.polymer.code.factory.JstFactory}
 *
 * @author Alexander Shabanov
 */
public final class JstFactoryTest {
  private JstFactory factory;

  @Before
  public void init() {
    factory = new DefaultJstFactory();
  }

  @Test
  public void shouldAllowMixingSynteticTypesAndClassesInSamePackage() {
    factory.jstClassType(String.class);
    factory.jstSynteticType(FqName.valueOf("java.lang.String2"));
  }

  @Test
  public void shouldCacheClassTypes() {
    final Jst.ClassType class1 = factory.jstClassType(Void.TYPE);
    final Jst.ClassType class2 = factory.jstClassType(String.class);
    assertEquals(class1, factory.jstClassType(Void.TYPE));
    assertEquals(class2, factory.jstClassType(String.class));
    assertEquals(String.class, class2.getWrappedClass());
  }

  @Test(expected = IllegalStateException.class)
  public void shouldDisallowClashesWithSynteticTypes() {
    factory.jstClassType(String.class);
    factory.jstSynteticType(FqName.valueOf("java.lang.String"));
  }

  @Test(expected = IllegalStateException.class)
  public void shouldDisallowClashesWithClassTypes() {
    factory.jstSynteticType(FqName.valueOf(JstFactoryTest.class.getName()));
    factory.jstClassType(JstFactoryTest.class);
  }
}
