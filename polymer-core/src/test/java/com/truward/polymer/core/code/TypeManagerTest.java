package com.truward.polymer.core.code;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenType;
import com.truward.polymer.core.support.code.DefaultTypeManager;
import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Shabanov
 */
public final class TypeManagerTest {

  private final FqName currentPackage = FqName.parse("com.company.product");

  private TypeManager typeManager;

  @Before
  public void init() {
    typeManager = new DefaultTypeManager();
    typeManager.setPackageName(currentPackage);
  }

  @Test
  public void shouldProduceNoImportsForNoInput() {
    typeManager.freeze();
    assertTrue(typeManager.getImportNames().isEmpty());
  }

  @Test
  public void shouldProduceNoImportForVisibleByDefault() {
    final GenType[] genTypes = {
        typeManager.adaptType(int.class),
        typeManager.adaptType(void.class),
        typeManager.adaptType(String.class),
        typeManager.adaptType(Cloneable[].class),
        typeManager.adaptType(getReturnTypeOf("dummy1")),
    };

    assertEquals(genTypes.length, ImmutableSet.copyOf(genTypes).size()); // there should be no duplicates

    typeManager.freeze();
    assertTrue(typeManager.getImportNames().isEmpty());
    assertAllVisible(genTypes[0], genTypes[1], genTypes[2]);
  }

  @Test
  public void shouldAllowMultipleAdaptationOfSameType() {
    final GenType[] genTypes = {
        typeManager.adaptType(List.class),
        typeManager.adaptType(List.class),
        typeManager.adaptType(List.class),
    };

    assertEquals(1, ImmutableSet.copyOf(genTypes).size()); // duplicates must fold

    typeManager.freeze();
    assertEquals(ImmutableList.of(FqName.parse("java.util.List")), typeManager.getImportNames());
  }

  @Test
  public void shouldProduceImports() {
    final GenType[] genTypes = {
        typeManager.adaptType(Map.class),
        typeManager.adaptType(getReturnTypeOf("dummy2")),
    };

    assertEquals(genTypes.length, ImmutableSet.copyOf(genTypes).size()); // there should be no duplicates

    typeManager.freeze();
    assertEquals(ImmutableSet.of(FqName.parse("java.util.Map"), FqName.parse("java.util.List"),
        FqName.parse("java.io.Serializable")), ImmutableSet.copyOf(typeManager.getImportNames()));
    assertAllVisible(genTypes[0]);
  }

  @Test
  public void shouldForbidNameClash() {
    final GenClass list1 = mockGenClass("my.util.List");
    final GenClass list2 = mockGenClass("my.another.List");
    final GenType[] genTypes = {
        typeManager.adaptType(List.class),
        typeManager.adaptType(list1),
        typeManager.adaptType(list2),
    };

    assertEquals(genTypes.length, ImmutableSet.copyOf(genTypes).size()); // there should be no duplicates

    typeManager.freeze();
    assertEquals(1, typeManager.getImportNames().size());

    int fqNameRequired = 0;
    for (final GenType genType : genTypes) {
      if (typeManager.isFqNameRequired((GenClass) genType)) {
        ++fqNameRequired;
      }
    }
    assertEquals(2, fqNameRequired);
  }

  //
  // Private
  //

  private void assertAllVisible(GenType... types) {
    for (final GenType type : types) {
      assertTrue("Type should be an instance of GenClass, got: " + type, type instanceof GenClass);
      assertTrue("Class " + type + " should be visible", !typeManager.isFqNameRequired((GenClass) type));
    }
  }

  private static GenClass mockGenClass(String fqName) {
    final GenClass result = mock(GenClass.class);
    when(result.getFqName()).thenReturn(FqName.parse(fqName));
    when(result.isPrimitive()).thenReturn(false);
    return result;
  }

  //
  // Dummy method
  //

  private Type getReturnTypeOf(String methodName) {
    try {
      return TypeManagerTest.class.getMethod(methodName, new Class[0]).getGenericReturnType();
    } catch (NoSuchMethodException e) {
      throw new AssertionError(e);
    }
  }

  @Deprecated @SuppressWarnings("UnusedDeclaration") public static ThreadLocal<Integer> dummy1() {
    throw new AssertionError();
  }

  @Deprecated @SuppressWarnings("UnusedDeclaration") public static List<Serializable> dummy2() {
    throw new AssertionError();
  }
}
