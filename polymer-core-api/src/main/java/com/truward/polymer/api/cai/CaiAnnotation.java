package com.truward.polymer.api.cai;

/**
 * @author Alexander Shabanov
 */
public interface CaiAnnotation extends CaiNode {
  boolean isModifier();

  boolean isPublic();

  boolean isPrivate();

  boolean isOverride();
}
