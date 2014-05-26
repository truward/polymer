package com.truward.polymer.specification.domain;

/**
 * @author Alexander Shabanov
 */
public enum DefensiveCopyStyle {
  /**
   * No defensive copying will be used.
   * This might make your object non-effectively immutable but you may gain some performance advantages.
   */
  NONE,

  /**
   * Use standard JDK classes to make defensive copy.
   * The following approach will be used to make an immutable copy:
   * <code>Collections.unmodifiableList(Arrays.asList($listVar.toArray(new String[$listVar.size()])))</code>
   */
  JDK,

  /**
   * Use google's guava to make defensive copy, i.e. <code>ImmutableList.copyOf($listVar)</code> which
   * is more performant comparing to the JDK approach.
   */
  GUAVA
}
