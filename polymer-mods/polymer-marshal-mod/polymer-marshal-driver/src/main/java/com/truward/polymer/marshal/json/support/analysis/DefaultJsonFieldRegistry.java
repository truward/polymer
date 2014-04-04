package com.truward.polymer.marshal.json.support.analysis;

import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.marshal.json.analysis.JsonField;
import com.truward.polymer.marshal.json.analysis.JsonFieldRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class DefaultJsonFieldRegistry implements JsonFieldRegistry {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Map<DomainField, JsonField> fieldMap = new HashMap<>();

  @Nonnull
  @Override
  public String getJsonName(@Nonnull DomainField domainField) {
    final JsonField field = getField(domainField);
    if (field != null) {
      if (field.hasJsonName()) {
        return field.getJsonName();
      } else {
        log.error("Domain field {} has associated JSON field with empty name", domainField);
      }
    }

    // fallback: default field name
    return domainField.getFieldName();
  }

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

    @Nonnull
    @Override
    public String getJsonName() {
      if (!hasJsonName()) {
        throw new IllegalStateException("No json name associated with this field");
      }
      return jsonName;
    }

    @Override
    public boolean hasJsonName() {
      return jsonName != null;
    }
  }
}
