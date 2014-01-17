package com.truward.polymer.core.output;

import javax.annotation.Nonnull;

/**
 * Represents common information about the particular file
 *
 * @author Alexander Shabanov
 */
public interface FileType {

  @Nonnull
  String getExtension();
}
