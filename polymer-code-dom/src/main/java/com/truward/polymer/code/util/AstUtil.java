package com.truward.polymer.code.util;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.code.Ast;
import com.truward.polymer.code.factory.AstFactory;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.util.List;

/**
 * Utility class for operating with AST nodes.
 *
 * @author Alexander Shabanov
 */
@Deprecated
public final class AstUtil {
  private AstUtil() {} // hidden

  public static final List<Modifier> PUBLIC_FINAL = ImmutableList.of(Modifier.PUBLIC, Modifier.FINAL);
  public static final List<Modifier> PRIVATE_FINAL = ImmutableList.of(Modifier.PRIVATE, Modifier.FINAL);

  @Nonnull
  public static <T extends Ast.NamedStmt<?>> T makePublicOverride(@Nonnull AstFactory factory, @Nonnull T instance) {
    instance.addAnnotation(factory.annotation(Override.class));
    instance.addModifiers(Modifier.PUBLIC);
    return instance;
  }
}
