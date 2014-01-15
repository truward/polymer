package com.truward.polymer.code.naming;

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
  /** Precalculated hash code, optimized for immutable FqName, default to 0 */
  private transient int hash;

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
    if (this == o) {
      return true;
    }
    if (!(o instanceof FqName)) {
      return false;
    }

    FqName other = (FqName) o;
    FqName self = this;
    for (;;) {
      if (!other.getName().equals(self.getName())) {
        return false;
      }

      other = other.parent;
      self = self.parent;
      if (other == self) {
        return true; // matched parents
      } else if (other == null || self == null) {
        return false;
      }
    }
  }

  @Override
  public int hashCode() {
    if (hash != 0) {
      return hash; // optimized access
    }

    int result = name.hashCode();
    for (FqName i = parent; i != null; i = i.parent) {
      result = 31 * result + i.name.hashCode();
    }

    hash = result;
    return hash;
  }


  @Override
  public String toString() {
    if (isRoot()) {
      return getName();
    }

    // append all name parts
    final int symCount = length();
    final StringBuilder builder = new StringBuilder(symCount);
    try {
      appendTo(builder);
    } catch (IOException e) {
      throw new IllegalStateException(e); // should never happen
    }

    assert builder.length() == symCount;
    return builder.toString();
  }

  /**
   * @return Count of chars in the toString representation, separated by dots
   */
  public int length() {
    int symCount = 0;
    FqName i = this;
    for (;; i = i.getParent()) {
      if (symCount > 0) {
        ++symCount; // separator char - 'dot'
      }
      symCount += i.getName().length();
      if (i.isRoot()) {
        break;
      }
    }
    return symCount;
  }

  public void appendTo(Appendable appendable) throws IOException {
    appendTo(appendable, '.');
  }

  public void appendTo(Appendable appendable, char separator) throws IOException {
    if (!isRoot()) {
      getParent().appendTo(appendable, separator);
      appendable.append(separator);
    }
    appendable.append(getName());
  }
}
