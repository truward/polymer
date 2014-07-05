package com.truward.polymer.api;

import com.truward.polymer.code.Jst;
import com.truward.polymer.naming.FqName;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class AppTest {

  interface ImplementationTarget {
    @Nonnull FqName getFqName();
  }

  interface ImplementationManager {
    @Nullable ImplementationTarget getTarget(@Nonnull Class<?> clazz);
  }

  interface ClassManager {
    @Nonnull List<GenTarget> getTargets(@Nonnull Class<?> clazz);
  }

  interface GenMethod {
    @Nonnull Method getOriginMethod();

    @Nonnull Jst.MethodDeclaration getMethodDeclaration();

    @Nonnull GenTarget getParentTarget();
  }

  interface GenTarget {
    @Nonnull Class<?> getOriginClass();

    @Nonnull List<GenMethod> getAllMethods();

    @Nonnull Jst.Unit getDeclarationUnit();

    @Nonnull Jst.ClassDeclaration getClassDeclaration();
  }

  interface DomainField {

  }

  // serializer: find all the fields of the particular generation target


  private void f(Class<?> clazz) {
    final Class<?> o = clazz.getEnclosingClass();
    System.out.println("o = " + o);
  }


  @Test
  public void dummyTest() {
    f(Jst.Expression.class);
    assertTrue(true);
  }
}
