package com.truward.polymer.core.naming;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Represents fully qualified name
 *
 * @author Alexander Shabanov
 */
public final class FqName {
  private final String name;
  private final FqName parent;

  public static FqName parse(String fqName) {
    int dotIndex;
    int nextIndex = 0;

    FqName next = null;
    for (;;) {
      dotIndex = fqName.indexOf('.', nextIndex);
      final String part;
      if (dotIndex < 0) {
        part = fqName.substring(nextIndex);
      } else {
        part = fqName.substring(nextIndex, dotIndex);
        nextIndex = dotIndex + 1;
      }
      next = new FqName(part, next);

      if (dotIndex < 0) {
        break;
      }
    }

    return next;
  }

  public FqName(@Nonnull String name, @Nullable FqName parent) {
    this.name = name;
    this.parent = parent;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Nonnull
  public FqName getParent() {
    if (parent == null) {
      throw new IllegalStateException("There is no parent of the root fqName");
    }
    return parent;
  }

  public boolean isRoot() {
    return parent == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FqName fqName = (FqName) o;

    return name.equals(fqName.name) && !(parent != null ? !parent.equals(fqName.parent) : fqName.parent != null);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + (parent != null ? parent.hashCode() : 0);
    return result;
  }


  @Override
  public String toString() {
    if (isRoot()) {
      return getName();
    }

    // calc symbols count
    int symCount = 0;
    FqName i = this;
    for (;; i = i.getParent()) {
      if (symCount > 0) {
        ++symCount; // count dot
      }
      symCount += i.getName().length();
      if (i.isRoot()) {
        break;
      }
    }

    // append all name parts
    final StringBuilder builder = new StringBuilder(symCount);
    try {
      appendTo(builder);
    } catch (IOException e) {
      throw new IllegalStateException(e); // should never happen
    }

    assert builder.length() == symCount;
    return builder.toString();
  }

  public void appendTo(Appendable appendable) throws IOException {
    appendTo(appendable, '.');
  }

  public void appendTo(Appendable appendable, char separator) throws IOException {
    if (!isRoot()) {
      getParent().appendTo(appendable);
      appendable.append(separator);
    }
    appendable.append(getName());
  }
}
