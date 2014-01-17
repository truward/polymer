package com.truward.polymer.core.generator.model;

/**
 * Represents primitive node that provides plain text as its representation in the target language.
 */
public interface Text extends CodeObject {
  String getText();
}
