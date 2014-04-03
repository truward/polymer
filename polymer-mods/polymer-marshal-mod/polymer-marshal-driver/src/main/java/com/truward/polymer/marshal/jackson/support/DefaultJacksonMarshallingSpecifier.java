package com.truward.polymer.marshal.jackson.support;

import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenClassReference;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.util.Assert;
import com.truward.polymer.marshal.json.JacksonMarshallingSpecifier;
import com.truward.polymer.marshal.json.analysis.JsonTarget;
import com.truward.polymer.marshal.json.support.AbstractJsonMarshallerSpecifier;
import com.truward.polymer.naming.FqName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class DefaultJacksonMarshallingSpecifier extends AbstractJsonMarshallerSpecifier implements JacksonMarshallingSpecifier {
  private boolean mappersRequired;
  private final Map<JsonTarget, GenClass> serializerClasses = new HashMap<>();
  private final Map<JsonTarget, GenClass> deserializerClasses = new HashMap<>();

  public DefaultJacksonMarshallingSpecifier() {
    this.mappersRequired = true;
  }

  @Override
  protected void finalizeAnalysis() {
    Assert.nonNull(getTargetClassName(), "Target class name expected to be non-null");

    if (mappersRequired) {
      for (final JsonTarget target : getDomainClassToJsonTarget().values()) {
        final String simpleName = target.getDomainClass().getOrigin().getOriginClass().getSimpleName();

        if (target.isReaderSupportRequested() && !deserializerClasses.containsKey(target)) {
          deserializerClasses.put(target, GenClassReference.from(new FqName(simpleName + "Deserializer",
              getTargetClassName())));
        }

        if (target.isWriterSupportRequested() && !serializerClasses.containsKey(target)) {
          serializerClasses.put(target, GenClassReference.from(new FqName(simpleName + "Serializer",
              getTargetClassName())));
        }
      }
    }

    log.info("Json marshaller analysis has been completed");
  }

  @Override
  protected void generateCode(GenInlineBlock bodyStream) {
    // generate code
    final JacksonBinderGenerator binderImplementer = new JacksonBinderGenerator(getTargetClassName(),
        bodyStream, getDomainClassToJsonTarget());
    binderImplementer.generate();
  }
}
