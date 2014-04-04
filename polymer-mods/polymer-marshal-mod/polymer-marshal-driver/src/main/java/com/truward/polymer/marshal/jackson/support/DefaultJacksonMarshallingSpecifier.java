package com.truward.polymer.marshal.jackson.support;

import com.truward.polymer.core.code.typed.GenClassReference;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.util.Assert;
import com.truward.polymer.marshal.json.JacksonMarshallingSpecifier;
import com.truward.polymer.marshal.json.analysis.JsonTarget;
import com.truward.polymer.marshal.json.support.AbstractJsonMarshallerSpecifier;

/**
 * @author Alexander Shabanov
 */
public final class DefaultJacksonMarshallingSpecifier extends AbstractJsonMarshallerSpecifier implements JacksonMarshallingSpecifier {
  private boolean mappersRequired;

  public DefaultJacksonMarshallingSpecifier() {
    this.mappersRequired = true;
  }

  @Override
  protected void finalizeAnalysis() {
    Assert.nonNull(getTargetClassName(), "Target class name expected to be non-null");

    if (mappersRequired) {
      for (final JsonTarget target : getDomainClassToJsonTarget().values()) {
        final String simpleName = target.getDomainClass().getOrigin().getOriginClass().getSimpleName();

        if (target.isReaderSupportRequested() && target.getTargetReaderClass() == null) {
          target.setTargetReaderClass(GenClassReference.from(getTargetClassName().append(simpleName + "Deserializer")));
        }

        if (target.isWriterSupportRequested() && target.getTargetWriterClass() == null) {
          target.setTargetWriterClass(GenClassReference.from(getTargetClassName().append(simpleName + "Serializer")));
        }
      }
    }

    log.info("Json marshaller analysis has been completed");
  }

  @Override
  protected void generateCode(GenInlineBlock bodyStream) {
    // generate code
    final JacksonBinderGenerator binderImplementer = new JacksonBinderGenerator(getTargetClassName(),
        bodyStream, getDomainClassToJsonTarget(), getFieldRegistry());
    binderImplementer.generate();
  }
}
