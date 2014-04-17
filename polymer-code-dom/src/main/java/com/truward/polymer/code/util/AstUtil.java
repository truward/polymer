package com.truward.polymer.code.util;

import com.google.common.collect.ImmutableList;

import javax.lang.model.element.Modifier;
import java.util.List;

/**
 * Utility class for operating with AST nodes.
 *
 * @author Alexander Shabanov
 */
public final class AstUtil {
  private AstUtil() {} // hidden

  public static final List<Modifier> PUBLIC_FINAL = ImmutableList.of(Modifier.PUBLIC, Modifier.FINAL);
  public static final List<Modifier> PRIVATE_FINAL = ImmutableList.of(Modifier.PRIVATE, Modifier.FINAL);
}
