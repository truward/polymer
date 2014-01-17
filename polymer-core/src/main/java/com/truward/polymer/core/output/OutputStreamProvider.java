package com.truward.polymer.core.output;

import com.truward.polymer.code.naming.FqName;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Service, that provides its clients with the output stream, that corresponds to the provided resources
 *
 * @author Alexander Shabanov
 */
public interface OutputStreamProvider {

  /**
   * Creates a new resource by using the qualified name of the class/resource.
   * The created output stream will be associated with the resource, generated at the location, that
   * corresponds to the provided qualified name + extension, i.e. given the name 'com.mysite.App' and
   * extension 'java' the output file will be generated at $targetDir/com/mysite/App.java
   *
   * @param name Qualified entity name
   * @param fileType Target file type, e.g. {@link com.truward.polymer.core.output.DefaultFileTypes#JAVA}
   * @return Created output stream
   * @throws IOException On I/O error
   */
  @Nonnull
  OutputStream createStreamForFile(@Nonnull FqName name, @Nonnull FileType fileType) throws IOException;
}
