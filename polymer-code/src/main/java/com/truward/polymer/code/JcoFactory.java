package com.truward.polymer.code;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class JcoFactory {
  private final Map<Class<?>, Jco.JavaClassRef> javaClassCache = new HashMap<>(500);

  @Nonnull
  public Jco.JavaClassRef ref(@Nonnull Class<?> klass) {
    Jco.JavaClassRef result = javaClassCache.get(klass);
    if (result == null) {
      result = new Jco.JavaClassRef(klass);
      javaClassCache.put(klass, result);
    }

    return result;
  }

  @Nonnull
  public Jco.ClassDeclRef ref(@Nonnull Jco.ClassDecl classDecl) {
    return new Jco.ClassDeclRef(classDecl);
  }
}
