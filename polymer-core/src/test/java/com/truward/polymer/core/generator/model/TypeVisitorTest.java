package com.truward.polymer.core.generator.model;

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
    final Class<?> clazz = String.class;
    assertEquals(clazz, TypeVisitor.apply(new TypeVisitor<Class<?>>() {
      @Override
      public Class<?> visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
        assertEquals(clazz, sourceType);
        return clazz;
      }
    }, clazz));
  }

  @Test
  public void shouldDetectArray() {
    final Class<?> clazz = int[].class;
    assertEquals(int.class, TypeVisitor.apply(new TypeVisitor<Class<?>>() {

      @Override
      public Class<?> visitArray(@Nonnull Type sourceType, @Nonnull Class<?> elementType) {
        assertEquals(clazz, sourceType);
        return elementType;
      }
    }, clazz));
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

  @Test
  public void shouldDetectLocalRef() {
    final String refName = "Local";
    assertEquals(refName, TypeVisitor.apply(new TypeVisitor<String>() {
      @Override
      public String visitLocalRef(@Nonnull Type sourceType, @Nonnull LocalRefType ref) {
        return ref.getText();
      }
    }, new LocalRefType(refName)));
  }


  /**
   * Dummy test method which is needed for {@link #shouldDetectGenericType()} to introduce parameterized typed.
   *
   * @return Test typed
   */
  @Ignore
  @Generated("For Reflection Test")
  public List<String> dummy() {
    return null;
  }
}
