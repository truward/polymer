package com.truward.polymer.marshal.gson.support;

import com.google.common.collect.ImmutableMap;
import com.truward.polymer.core.code.builder.CodeStream;
import com.truward.polymer.core.code.builder.CodeStreamSupport;
import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenClassReference;
import com.truward.polymer.core.code.typed.TypeVisitor;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.types.SynteticParameterizedType;
import com.truward.polymer.core.util.Assert;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.FieldUtil;
import com.truward.polymer.domain.analysis.OriginMethodRole;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.domain.analysis.support.Names;
import com.truward.polymer.marshal.json.GsonMarshallingSpecifier;
import com.truward.polymer.marshal.json.analysis.JsonTarget;
import com.truward.polymer.marshal.json.support.AbstractJsonMarshallerSpecifier;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import static com.truward.polymer.core.util.Assert.nonNull;

/**
 * @author Alexander Shabanov
 */

public final class DefaultGsonMarshallingSpecifier extends AbstractJsonMarshallerSpecifier implements GsonMarshallingSpecifier {

  @Override
  protected void finalizeAnalysis() {
    Assert.nonNull(getTargetClassName(), "Target class name expected to be non-null");

    for (final JsonTarget target : getDomainClassToJsonTarget().values()) {
      if (target.getTargetMarshallerClass() != null) {
        continue;
      }

      final String typeAdapterClassName = target.getDomainClass().getOrigin().getOriginClass().getSimpleName() +
          "TypeAdapter";
      target.setTargetMarshallerClass(GenClassReference.from(getTargetClassName().append(typeAdapterClassName)));
    }

    log.info("Json marshaller analysis has been completed");
  }

  @Override
  protected void generateCode(GenInlineBlock bodyStream) {
    // generate code
    final JsonBinderImplementer binderImplementer = new JsonBinderImplementer(getTargetClassName(),
        bodyStream, getDomainClassToJsonTarget());
    binderImplementer.generate();
  }

  @Override
  protected FqName getDefaultTargetClassName() {
    return FqName.parse("generated.GsonMarshaller");
  }

  //
  // Private
  //


  private static final class JsonBinderImplementer extends CodeStreamSupport {
    private final CodeStream codeStream;
    private final Map<GenDomainClass, JsonTarget> domainClassToJsonTarget;
    private final FqName fqName;

    private static final GenClass T_TYPE_ADAPTER = GenClassReference.from("com.google.gson.TypeAdapter");
    private static final GenClass T_JSON_READER = GenClassReference.from("com.google.gson.stream.JsonReader");
    private static final GenClass T_JSON_WRITER = GenClassReference.from("com.google.gson.stream.JsonWriter");
    //private static final GenClass T_JSON_TOKEN = GenClassReference.from("com.google.gson.stream.JsonToken");
    //private static final GenClass T_PARSE_EXCEPTION = GenClassReference.from("com.google.gson.JsonParseException");

    private static final String OUT_PARAM_NAME = "out";
    private static final String IN_PARAM_NAME = "in";

    private JsonBinderImplementer(@Nonnull FqName fqName,
                                  @Nonnull CodeStream codeStream,
                                  @Nonnull Map<GenDomainClass, JsonTarget> domainClassToJsonTarget) {
      this.fqName = fqName;
      this.codeStream = codeStream;
      this.domainClassToJsonTarget = ImmutableMap.copyOf(domainClassToJsonTarget);
    }

    @Nonnull
    public Collection<JsonTarget> getTargets() {
      return domainClassToJsonTarget.values();
    }

    @Nonnull
    @Override
    protected CodeStream getRootCodeStream() {
      return codeStream;
    }

    public void generate() {
      publicFinalClass().s(fqName.getName()).sp().c('{');

      for (final JsonTarget target : getTargets()) {
        generateTypeAdapterClass(target);
      }

      c('}').eol(); // class body end
    }

    private void generateTypeAdapterClass(@Nonnull JsonTarget target) {
      s("// type adapter " + target.getDomainClass().getOrigin().getOriginClass()).eol();

      final Class<?> originDomainClass = target.getDomainAnalysisResult().getOriginClass();
      //final GenClass domainClass = targets.getDomainClass();
      final String out = OUT_PARAM_NAME;
      final String in = IN_PARAM_NAME;
      final String value = Names.VALUE;

      // public static final {TypeAdapterClass}
      final GenClass typeAdapterClass = nonNull(target.getTargetMarshallerClass(), "No type adapter");
      publicStaticFinalClass().s(typeAdapterClass.getFqName().getName()).sp();
      // extends TypeAdapter<{DomainClass}>
      s("extends").sp().t(SynteticParameterizedType.from(T_TYPE_ADAPTER, originDomainClass)).sp();
      c('{'); // TypeAdapter class body

      // @Override public void write(JsonWriter out, {DomainClass} value) throws IOException {...}
      annotate(Override.class).s("public").sp().t(void.class).sp().s("write").c('(');
      var(T_JSON_WRITER, out).c(',').sp().var(originDomainClass, value);
      c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
      if (target.isWriterSupportRequested()) {
        s("writeObject").c('(').s(out).c(',').sp().s(value).c(')').c(';');
      } else {
        throwUnsupportedOperationException();
      }
      c('}').eol(); // End of write(JsonWriter out, {DomainClass} value)

      // public {DomainClass} read(JsonReader in) throws IOException {...}
      annotate(Override.class).s("public").sp().t(originDomainClass).sp().s("read").c('(');
      var(T_JSON_READER, IN_PARAM_NAME);
      c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
      if (target.isReaderSupportRequested()) {
        s("return").sp().s("readObject").c('(').s(in).c(')').c(';');
      } else {
        throwUnsupportedOperationException();
      }
      c('}').eol(); // End of write(JsonWriter out, {DomainClass} value)

      if (target.isWriterSupportRequested()) {
        // public static void writeObject(JsonWriter out, {DomainClass} value) throws IOException {...}
        s("public").sp().s("static").sp().t(void.class).sp().s("writeObject").c('(');
        var(T_JSON_WRITER, out).c(',').sp().var(originDomainClass, value);
        c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
        generateTypeAdapterWriteObjectBody(out, value, target);
        c('}').eol(); // End of writeObject(JsonWriter out, {DomainClass} value)
      }

      if (target.isReaderSupportRequested()) {
        // public static {DomainClass} readObject(JsonReader in) throws IOException {...}
        s("public").sp().s("static").sp().t(originDomainClass).sp().s("readObject").c('(');
        var(T_JSON_READER, IN_PARAM_NAME);
        c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
        generateTypeAdapterReadObjectBody();
        c('}'); // End of readObject(JsonReader in)
      }

      c('}'); // End of TypeAdapter class body
    }

    private void generateTypeAdapterWriteObjectBody(String out, String value, JsonTarget target) {
      dot(out, "beginObject").c('(', ')', ';');
      for (final DomainField field : target.getDomainAnalysisResult().getFields()) {
        final String getterName = FieldUtil.getMethodName(field, OriginMethodRole.GETTER);
        if (getterName == null) {
          throw new UnsupportedOperationException("Can't generate writeObject: field " + field +
              " has no associated getter");
        }

        if (FieldUtil.isNullable(field)) {
          // if field is null we can omit generating field entry
          s("if").c(' ', '(').callGetter(value, getterName).sps("!=").s("null");
          c(')', ' ', '{');
          generateFieldEntry(out, value, getterName, field);
          c('}');
        } else {
          generateFieldEntry(out, value, getterName, field);
        }
      }
      dot(out, "endObject").c('(', ')', ';');
    }

    private void generateFieldEntry(String out, String value, String getterName, DomainField field) {
      final String jsonName = field.getFieldName();

      // all the fields should have getter
      dot(out, "name").c('(').val(jsonName).c(')', ';');
      if (isDefaultValueSupportedForWrite(field.getFieldType())) {
        dot(out, "value").c('(').callGetter(value, getterName).c(')', ';');
      } else {
        throw new UnsupportedOperationException("Unsupported field type: " + field);
      }
    }

    private boolean isDefaultValueSupportedForWrite(@Nonnull Type type) {
      return TypeVisitor.apply(new TypeVisitor<Boolean>() {
        @Override
        public Boolean visitType(@Nonnull Type sourceType) {
          return false;
        }

        @Override
        public Boolean visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
          // JsonWriter.value() supports: boolean, double, long, String, Number
          return Number.class.isAssignableFrom(clazz) || String.class.equals(clazz) ||
              double.class.equals(clazz) || float.class.equals(clazz) ||
              byte.class.equals(clazz) || char.class.equals(clazz) || short.class.equals(clazz) ||
              int.class.equals(clazz) || long.class.equals(clazz) ||
              boolean.class.equals(clazz);
        }
      }, type);
    }

    private void generateTypeAdapterReadObjectBody() {
      throwUnsupportedOperationException();
    }

    /*
    Sample code:

    public final class AnimalTypeAdapter extends TypeAdapter<Animal> {
  @Override public void write(JsonWriter out, Animal value) throws IOException {
    writeObject(out, value);
  }

  @Override
  public Animal read(JsonReader in) throws IOException {
    return readObject(in);
  }

  public static void writeObject(JsonWriter out, Animal value) throws IOException {
    out.beginObject();
    out.name("id");
    out.value(value.getId());
    out.name("name");
    out.value(value.getName());
    out.name("description");
    DescriptionTypeAdapter.writeObject(out, value.getDescription());
    out.name("type");
    out.value(value.getType());
    out.endObject();
  }

  public static Animal readObject(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }

    int id = 0;
    String name = null;
    Description description = null;
    int type = 0;

    in.beginObject();
    while (in.hasNext()) {
      final String fieldName = in.nextName();
      if (fieldName.equals("id")) {
        id = in.nextInt();
      } else if (fieldName.equals("name")) {
        name = in.nextString();
      } else if (fieldName.equals("description")) {
        description = DescriptionTypeAdapter.readObject(in);
      } else if (fieldName.equals("type")) {
        type = in.nextInt();
      }else {
        // WARN: unknown field
        System.err.println("Unknown field " + fieldName + " for Animal object");
      }
    }
    in.endObject();

    if (name == null || description == null) {
      throw new JsonParseException("No name or description");
    }

    return new Animal(id, name, description, type);
  }
}

     */
  }
}
