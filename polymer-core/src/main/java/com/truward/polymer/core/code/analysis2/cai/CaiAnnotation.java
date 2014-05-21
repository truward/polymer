package com.truward.polymer.core.code.analysis2.cai;

/**
 * @author Alexander Shabanov
 */
public interface CaiAnnotation extends CaiNode {
    boolean isModifier();

    boolean isPublic();

    boolean isPrivate();

    boolean isOverride();
}
