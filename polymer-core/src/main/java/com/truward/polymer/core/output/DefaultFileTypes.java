package com.truward.polymer.core.output;

import javax.annotation.Nonnull;

/**
 * Contains common file extensions
 *
 * @author Alexander Shabanov
 */
public enum DefaultFileTypes implements FileType {
  NONE(""),
  JAVA("java"),
  PROPERTIES("properties"),
  JS("js"),
  XML("xml"),
  JSON("json"),
  SQL("sql"),
  TXT("txt"),
  HTML("html");
  
  @Nonnull
  private final String extension;

  @Override
  @Nonnull
  public String getExtension() {
    return extension;
  }

  private DefaultFileTypes(@Nonnull String extension) {
    this.extension = extension;
  }
}
