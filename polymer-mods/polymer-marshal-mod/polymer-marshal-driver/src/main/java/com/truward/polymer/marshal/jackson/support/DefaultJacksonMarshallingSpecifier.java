package com.truward.polymer.marshal.jackson.support;

import com.truward.polymer.core.code.typed.GenClassReference;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.util.Assert;
import com.truward.polymer.marshal.json.JacksonMarshallingSpecifier;
import com.truward.polymer.marshal.json.analysis.JsonTarget;
import com.truward.polymer.marshal.json.support.AbstractJsonMarshallerSpecifier;
import com.truward.polymer.naming.FqName;

/**
 * @author Alexander Shabanov
 */
public final class DefaultJacksonMarshallingSpecifier extends AbstractJsonMarshallerSpecifier implements JacksonMarshallingSpecifier {
  private boolean mappersRequired;

  public DefaultJacksonMarshallingSpecifier() {
    this.mappersRequired = true;
  }

  @Override
  public FqName getDefaultTargetClassName() {
    return FqName.parse("generated.JacksonMarshaller");
  }

  @Override
  protected void finalizeAnalysis() {
    Assert.nonNull(getTargetClassName(), "Target class name expected to be non-null");

    if (mappersRequired) {
      for (final JsonTarget target : getDomainClassToJsonTarget().values()) {
        final Class<?> originClass = target.getDomainClass().getOrigin().getOriginClass();
        log.debug("Performing JSON analysis for {}", originClass);

        final String simpleName = originClass.getSimpleName();

        if (target.isReaderSupportRequested() && target.getTargetReaderClass() == null) {
          final FqName deserializerName = getTargetClassName().append(simpleName + "Deserializer");
          log.debug("Adding deserializer {} for {}", deserializerName, originClass);
          target.setTargetReaderClass(GenClassReference.from(deserializerName));
        }

        if (target.isWriterSupportRequested() && target.getTargetWriterClass() == null) {
          final FqName serializerName = getTargetClassName().append(simpleName + "Serializer");
          log.debug("Adding serializer {} for {}", serializerName, originClass);
          target.setTargetWriterClass(GenClassReference.from(serializerName));
        }
      }
    }

    log.info("Jackson-json marshaller analysis has been completed");
  }

  @Override
  protected void generateCode(GenInlineBlock bodyStream) {
    // generate code
    final JacksonBinderGenerator binderImplementer = new JacksonBinderGenerator(getTargetClassName(),
        bodyStream, getDomainClassToJsonTarget(), getFieldRegistry());
    binderImplementer.generate();
  }
}
