package com.truward.polymer.api.output;

import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.FileType;

/**
 * @author Alexander Shabanov
 */
public interface OutputRegistry {
  void locate(FileType fileType, FqName fileName);
}
