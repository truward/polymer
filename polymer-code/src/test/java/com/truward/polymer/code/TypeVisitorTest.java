package com.truward.polymer.code;

import com.google.common.collect.ImmutableList;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Shabanov
 */
public final class TypeVisitorTest {

  @Test
  public void shouldDetectClass() {
    final Class<?> klass = String.class;
    assertEquals(klass, TypeVisitor.apply(new TypeVisitor<Class<?>>() {
      @Override
      public Class<?> visitClass(@Nonnull Type sourceType, @Nonnull Class<?> klass) {
        assertEquals(klass, sourceType);
        return klass;
      }
    }, klass));
  }

  @Test
  public void shouldDetectArray() {
    final Class<?> klass = int[].class;
    assertEquals(int.class, TypeVisitor.apply(new TypeVisitor<Class<?>>() {

      @Override
      public Class<?> visitArray(@Nonnull Type sourceType, @Nonnull Class<?> elementType) {
        assertEquals(klass, sourceType);
        return elementType;
      }
    }, klass));
  }

  @Test
  public void shouldDetectGenericType() throws NoSuchMethodException {
    final Type type = TypeVisitorTest.class.getMethod("dummy", new Class[0]).getGenericReturnType();
    assertEquals(ImmutableList.<Type>of(String.class), TypeVisitor.apply(new TypeVisitor<List<Type>>() {

      @Override
      public List<Type> visitGenericType(@Nonnull Type sourceType, @Nonnull Class<?> rawType, @Nonnull List<Type> args) {
        assertEquals(type, sourceType);
        assertEquals(List.class, rawType);
        return args;
      }
    }, type));
  }


  /**
   * Dummy test method which is needed for {@link #shouldDetectGenericType()} to introduce parameterized type.
   *
   * @return Test type
   */
  @Ignore
  @Generated("For Reflection Test")
  public List<String> dummy() {
    return null;
  }
}
