package com.truward.polymer.core.code.builder;

import com.truward.polymer.core.code.GenObject;
import com.truward.polymer.freezable.Freezable;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents code output stream
 *
 * @author Alexander Shabanov
 */
public interface CodeStream extends CodeFactory, Freezable {

  // char
  @Nonnull CodeStream c(char ch);

  @Nonnull CodeStream c(@Nonnull char... chars);

  // string
  @Nonnull CodeStream s(@Nonnull String string);

  @Nonnull CodeStream s(@Nonnull String... strings);

  @Nonnull CodeStream s(@Nonnull FqName fqName);

  // space
  @Nonnull CodeStream sp();

  // end-of-line
  @Nonnull CodeStream eol();

  // types
  @Nonnull CodeStream t(@Nonnull Type type);

  // code object
  @Nonnull CodeStream obj(@Nonnull GenObject obj);

  /**
   * @return Returns list of childs added to this stream.
   */
  @Nonnull List<GenObject> getChilds();
}
