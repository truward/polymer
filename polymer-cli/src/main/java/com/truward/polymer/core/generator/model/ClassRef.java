package com.truward.polymer.core.generator.model;

/**
 * @author Alexander Shabanov
 */
public final class ClassRef implements Text {
  private final Class<?> originClass;
  private Boolean simpleNameEnabled;

  public ClassRef(Class<?> originClass) {
    if (originClass == null) {
      throw new IllegalArgumentException("Null origin class");
    }
    this.originClass = originClass;
    if (isVisibleByDefault()) {
      this.simpleNameEnabled = true;
    }
  }

  public Class<?> getOriginClass() {
    return originClass;
  }

  public String getSimpleName() {
    return getOriginClass().getSimpleName();
  }

  public String getQualifiedName() {
    return getOriginClass().getCanonicalName();
  }

  public boolean isVisibleByDefault() {
    return originClass.isPrimitive() || originClass.getPackage().getName().equals("java.lang");
  }

  public boolean isSimpleNameEnabled() {
    if (this.simpleNameEnabled == null) {
      throw new IllegalStateException("Unable to get name that is not set");
    }
    return simpleNameEnabled;
  }

  public void setSimpleNameEnabled(boolean simpleNameEnabled) {
    if (this.simpleNameEnabled != null) {
      throw new IllegalStateException("Unable to set name twice");
    }
    this.simpleNameEnabled = simpleNameEnabled;
  }

  @Override
  public String getText() {
    if (isSimpleNameEnabled()) {
      return originClass.getSimpleName();
    }

    return originClass.getCanonicalName();
  }
}
