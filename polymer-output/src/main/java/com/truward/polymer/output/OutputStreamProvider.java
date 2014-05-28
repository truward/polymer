package com.truward.polymer.output;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Service, that provides its clients with the output stream, that corresponds to the provided resources
 *
 * @author Alexander Shabanov
 */
public interface OutputStreamProvider {

  /** Character set that should be used for all the text output unless there are very good reasons for not doing so */
  static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  /**
   * Creates a new resource by using the qualified name of the class/resource.
   * The created output stream will be associated with the resource, generated at the location, that
   * corresponds to the provided qualified name + extension, i.e. given the name 'com.mysite.App' and
   * extension 'java' the output file will be generated at $targetDir/com/mysite/App.java
   *
   * @param name Qualified entity name
   * @param fileType Target file typed, e.g. {@link StandardFileType#JAVA}
   * @return Created output stream
   * @throws IOException On I/O error
   */
  @Nonnull
  OutputStream createStreamForFile(@Nonnull FqName name, @Nonnull FileType fileType) throws IOException;
}
