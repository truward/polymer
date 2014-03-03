package com.truward.polymer.marshal.gson.support.analysis;

import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.marshal.gson.analysis.GsonField;
import com.truward.polymer.marshal.gson.analysis.GsonFieldRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class DefaultGsonFieldRegistry implements GsonFieldRegistry {
  private final Map<DomainField, GsonField> fieldMap = new HashMap<>();

  @Nullable
  @Override
  public GsonField getField(@Nonnull DomainField domainField) {
    return fieldMap.get(domainField);
  }

  @Override
  public void putField(@Nonnull DomainField domainField, @Nonnull GsonField gsonField) {
    GsonField prev = fieldMap.get(domainField);
    if (prev != null) {
      throw new IllegalStateException("Detected clash with previous field: " + prev + ", current: " + gsonField);
    }

    fieldMap.put(domainField, gsonField);
  }

  @Nonnull
  @Override
  public GsonField adapt(@Nonnull DomainField domainField) {
    GsonField gsonField = fieldMap.get(domainField);
    if (gsonField == null) {
      gsonField = new DefaultGsonField(domainField.getFieldName());
      fieldMap.put(domainField, gsonField);
    }

    return gsonField;
  }

  //
  // Private
  //

  private static final class DefaultGsonField implements GsonField {
    private String jsonName;

    private DefaultGsonField() {
    }

    private DefaultGsonField(String jsonName) {
      this();
      setJsonName(jsonName);
    }

    @Override
    public void setJsonName(@Nonnull String name) {
      this.jsonName = name;
    }

    @Nullable
    @Override
    public String getJsonName() {
      return jsonName;
    }
  }
}
