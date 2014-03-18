package com.truward.polymer.marshal.json.support.analysis;

import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.marshal.json.analysis.JsonField;
import com.truward.polymer.marshal.json.analysis.JsonFieldRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class DefaultJsonFieldRegistry implements JsonFieldRegistry {
  private final Map<DomainField, JsonField> fieldMap = new HashMap<>();

  @Nullable
  @Override
  public JsonField getField(@Nonnull DomainField domainField) {
    return fieldMap.get(domainField);
  }

  @Override
  public void putField(@Nonnull DomainField domainField, @Nonnull JsonField gsonField) {
    JsonField prev = fieldMap.get(domainField);
    if (prev != null) {
      throw new IllegalStateException("Detected clash with previous field: " + prev + ", current: " + gsonField);
    }

    fieldMap.put(domainField, gsonField);
  }

  @Nonnull
  @Override
  public JsonField adapt(@Nonnull DomainField domainField) {
    JsonField gsonField = fieldMap.get(domainField);
    if (gsonField == null) {
      gsonField = new DefaultJsonField(domainField.getFieldName());
      fieldMap.put(domainField, gsonField);
    }

    return gsonField;
  }

  //
  // Private
  //

  private static final class DefaultJsonField implements JsonField {
    private String jsonName;

    private DefaultJsonField() {
    }

    private DefaultJsonField(String jsonName) {
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
