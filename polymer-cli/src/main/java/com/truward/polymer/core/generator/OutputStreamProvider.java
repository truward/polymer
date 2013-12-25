package com.truward.polymer.core.generator;

import com.truward.polymer.core.naming.FqName;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Alexander Shabanov
 */
public interface OutputStreamProvider {
  OutputStream createStreamForFile(FqName name, String extension) throws IOException;
}
