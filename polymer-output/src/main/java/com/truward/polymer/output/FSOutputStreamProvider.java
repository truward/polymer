package com.truward.polymer.output;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Alexander Shabanov
 */
public final class FSOutputStreamProvider implements OutputStreamProvider {
  private final File baseDir;

  public FSOutputStreamProvider(@Nonnull File baseDir) throws IOException {
    this.baseDir = baseDir.getCanonicalFile();
  }

  @Override
  @Nonnull
  public OutputStream createStreamForFile(@Nonnull FqName name, @Nonnull FileType fileType) throws IOException {
    final StringBuilder fileNameBuilder = new StringBuilder(500);
    final char separator = File.separatorChar;

    // construct name to the parent dir and ensure it does exist
    fileNameBuilder.append(baseDir.getPath()).append(separator);
    if (!name.isRoot()) {
      name.getParent().appendTo(fileNameBuilder, separator);
    }
    final String parentDirPath = fileNameBuilder.toString();
    final File parentDir = new File(parentDirPath);
    if (!parentDir.mkdirs() && !parentDir.exists()) {
      throw new IOException("Unable to create/access to the parent directory");
    }

    fileNameBuilder.append(separator).append(name.getName());
    if (!fileType.getExtension().isEmpty()) {
      fileNameBuilder.append('.').append(fileType.getExtension());
    }
    final String fileName = fileNameBuilder.toString();
    return new FileOutputStream(fileName);
  }
}
