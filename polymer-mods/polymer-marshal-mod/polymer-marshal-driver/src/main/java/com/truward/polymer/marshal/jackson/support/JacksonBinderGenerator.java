package com.truward.polymer.marshal.jackson.support;

import com.google.common.collect.BiMap;
import com.truward.polymer.core.code.GenObject;
import com.truward.polymer.core.code.builder.CodeStream;
import com.truward.polymer.core.code.builder.CodeStreamSupport;
import com.truward.polymer.core.code.builder.SimpleDelegatingCodeStream;
import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenClassReference;
import com.truward.polymer.core.code.typed.TypeVisitor;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.types.DefaultValues;
import com.truward.polymer.core.types.SynteticParameterizedType;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.FieldUtil;
import com.truward.polymer.domain.analysis.OriginMethodRole;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.marshal.json.analysis.JsonFieldRegistry;
import com.truward.polymer.marshal.json.analysis.JsonTarget;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import static com.truward.polymer.util.Assert.nonNull;

/**
 * Contains implementer of the jackson serialization/deserialization code.
 *
 * @author Alexander Shabanov
 */
final class JacksonBinderGenerator extends CodeStreamSupport {
  private final Logger log = LoggerFactory.getLogger(JacksonBinderGenerator.class);

  // jackson serializer classes
  private static final GenClass T_JSON_GENERATOR = GenClassReference.from("com.fasterxml.jackson.core.JsonGenerator");
  private static final GenClass T_JSON_SERIALIZER = GenClassReference.from("com.fasterxml.jackson.databind.JsonSerializer");
  private static final GenClass T_SERIALIZER_PROVIDER = GenClassReference.from("com.fasterxml.jackson.databind.SerializerProvider");

  // jackson deserializer classes
  private static final GenClass T_JSON_PARSER = GenClassReference.from("com.fasterxml.jackson.core.JsonParser");
  private static final GenClass T_JSON_DESERIALIZER = GenClassReference.from("com.fasterxml.jackson.databind.JsonDeserializer");
  private static final GenClass T_DESERIALIZATION_CONTEXT = GenClassReference.from(
      "com.fasterxml.jackson.databind.DeserializationContext");
  private static final GenClass T_JSON_TOKEN = GenClassReference.from("com.fasterxml.jackson.core.JsonToken");

  // jackson object mapper
  private static final GenClass T_OBJECT_MAPPER = GenClassReference.from("com.fasterxml.jackson.databind.ObjectMapper");

  // jackson generic classes
  private static final GenClass T_SIMPLE_MODULE = GenClassReference.from("com.fasterxml.jackson.databind.module.SimpleModule");
  private static final GenClass T_TYPE_REFERENCE = GenClassReference.from("com.fasterxml.jackson.core.type.TypeReference");

  private final BiMap<GenDomainClass, JsonTarget> domainClassToJsonTarget;
  private final CodeStream codeStream;
  private final FqName fqName;

  private final JacksonMarshallerVars v;
  private final JsonFieldRegistry fieldRegistry;

  public JacksonBinderGenerator(@Nonnull FqName fqName, @Nonnull CodeStream codeStream,
                                @Nonnull BiMap<GenDomainClass, JsonTarget> domainClassToJsonTarget,
                                @Nonnull JsonFieldRegistry fieldRegistry) {
    this.fqName = fqName;
    this.codeStream = codeStream;
    this.domainClassToJsonTarget = domainClassToJsonTarget;
    this.fieldRegistry = fieldRegistry;
    this.v = new JacksonMarshallerVars(this);
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

    // private constructor (Utility class)
    s("/** Hidden constructor */").eol();
    s("public").sp().s(fqName.getName()).c('(', ')').sp().c('{').c('}').eol();

    generateAttachMethod();

    for (final JsonTarget target : getTargets()) {
      eol();
      generateStaticMarshallers(target);
    }

    c('}').eol(); // class body end
  }

  private void generateStaticMarshallers(@Nonnull JsonTarget target) {
    generateSerializerSupport(target);
    generateDeserializerSupport(target);
  }

  @Nonnull
  private String getJsonName(@Nonnull DomainField field) {
    return fieldRegistry.getJsonName(field);
  }

  //
  // Attach Method (Binds Models to Marshallers)
  //

  private void generateAttachMethod() {
    final String moduleNameVal = fqName.getName();

    // public static void attachMarshallersTo(ObjectMapper mapper)
    s("public").sp().s("static").sp().t(void.class).sp().s(v.attachMarshallersTo).c('(');
    t(T_OBJECT_MAPPER).sp().s(v.mapper).c(')', '{');

    // final SimpleModule module = new SimpleModule("JacksonMarshaller");
    s("final").sp().t(T_SIMPLE_MODULE).sp().s(v.module).sp().c('=').sp();
    newType(T_SIMPLE_MODULE).c('(').val(moduleNameVal).c(')', ';');

    // add respective serializer and deserializer
    for (final JsonTarget jsonTarget : getTargets()) {
      final Class<?> domainClass = jsonTarget.getDomainClass().getOrigin().getOriginClass();

      if (jsonTarget.isWriterSupportRequested()) {
        // module.addSerializer({DomainClass}.class, new {Serializer}());
        dot(v.module, "addSerializer").c('(').t(domainClass).c('.').s("class").commaSp();
        newType(nonNull(jsonTarget.getTargetWriterClass(), "No writer")).c('(', ')');
        c(')', ';');
      }

      if (jsonTarget.isReaderSupportRequested()) {
        // module.addDeserializer({DomainClass}.class, new {Serializer}());
        dot(v.module, "addDeserializer").c('(').t(domainClass).c('.').s("class").commaSp();
        newType(nonNull(jsonTarget.getTargetReaderClass(), "No reader")).c('(', ')');
        c(')', ';');
      }
    }

    dot(v.mapper, "registerModule").c('(').s(v.module).c(')', ';');
    c('}');
  }


  //
  // Serializer (Writer)
  //

  private void generateSerializerSupport(@Nonnull JsonTarget target) {
    if (!target.isWriterSupportRequested()) {
      return;
    }

    s("// Serializer for class " + target.getDomainClass().getOrigin().getOriginClass()).eol();

    final Class<?> originDomainClass = target.getDomainAnalysisResult().getOriginClass();
    final GenClass writerClass = nonNull(target.getTargetWriterClass(), "Writer class is null");

    // public static final class {Serializer} extends JsonSerializer<{DomainClass}> {}
    s("public").sp().s("static").sp().s("final").sp();
    s("class").sp().s(writerClass.getFqName().getName()).sp();
    s("extends").sp().t(SynteticParameterizedType.from(T_JSON_SERIALIZER, originDomainClass)).sp();
    // class body
    c('{');

    // @Override public void serialize({DomainClass} value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException
    annotate(Override.class).s("public").sp().t(void.class).sp().s("serialize");
    c('(').t(originDomainClass).sp().s(v.value).commaSp().t(T_JSON_GENERATOR).sp().s(v.jsonGenerator).commaSp();
    t(T_SERIALIZER_PROVIDER).sp().s("provider").c(')').sp().s("throws").sp().t(IOException.class).sp();
    c('{'); // start body
    dot(v.jsonGenerator, "writeStartObject").c('(', ')', ';');
    s("writeBody").c('(').s(v.jsonGenerator).commaSp().s(v.value).c(')', ';');
    dot(v.jsonGenerator, "writeEndObject").c('(', ')', ';');
    c('}'); // end of serialize method

    eol(); // separator between methods

    // public static void writeBody(JsonGenerator jsonGenerator, {DomainClass} value) throws IOException {...}
    s("public").sp().s("static").sp().t(void.class).sp().s(v.writeBody).c('(');
    var(T_JSON_GENERATOR, v.jsonGenerator).commaSp().var(originDomainClass, v.value);
    c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
    generateWriteMethodBody(target);
    c('}'); // End of writeBody(JsonWriter jsonGenerator, {DomainClass} value)

    c('}').eol(); // end of Serializer class body
  }

  private void generateWriteMethodBody(JsonTarget target) {
    for (final DomainField field : target.getDomainAnalysisResult().getFields()) {
      generateFieldEntry(field);
    }
  }

  private GenInlineBlock newGetterCall(DomainField field) {
    final String getterName = FieldUtil.getMethodName(field, OriginMethodRole.GETTER);
    if (getterName == null) {
      throw new UnsupportedOperationException("Can't generate writeObject: field " + field +
          " has no associated getter");
    }
    final GenInlineBlock getterCall = newInlineBlock();
    new SimpleDelegatingCodeStream(getterCall).callGetter(v.value, getterName);
    return getterCall;
  }

  private void generateFieldEntry(final DomainField field) {
    final GenInlineBlock getterCall = newGetterCall(field);
    final String jsonName = getJsonName(field);

    TypeVisitor.apply(new TypeVisitor<Void>() {
      @Override
      public Void visitType(@Nonnull Type sourceType) {
        throw new UnsupportedOperationException("Unknown type?");
      }

      @Override
      public Void visitArray(@Nonnull Type sourceType, @Nonnull Type elementType) {
        generateWriteFieldForCollection(jsonName, getterCall, elementType);
        return null;
      }

      @Override
      public Void visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
        generateWriteFieldForClass(jsonName, getterCall, clazz);
        return null;
      }

      @Override
      public Void visitGenClass(@Nonnull Type sourceType, @Nonnull GenClass genClass) {
        // TODO: find deserializer by gen class
        generateWriteFieldAsObject(jsonName, getterCall);
        return null;
      }

      @Override
      public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Type rawType, @Nonnull List<? extends Type> args) {
        generateWriteFieldForGeneric(jsonName, getterCall, rawType, args);
        return null;
      }
    }, field.getFieldType());
  }

  private void generateWriteFieldAsObject(String jsonName, GenObject var) {
    dot(v.jsonGenerator, "writeObjectField").c('(').val(jsonName).c(',', ' ').obj(var).c(')', ';');
  }

  private void generateWriteFieldForGeneric(String jsonName, GenObject var, Type rawType, List<? extends Type> args) {
    if (rawType instanceof Class) {
      final Class<?> clazz = (Class<?>) rawType;
      if (Iterable.class.isAssignableFrom(clazz)) {
        Assert.state(args.size() == 1, "Iterable should have one argument");
        generateWriteFieldForCollection(jsonName, var, args.get(0));
        return;
      }
    }

    // fallback: write as object
    dot(v.jsonGenerator, "writeObjectField").c('(').val(jsonName).c(',', ' ').obj(var).c(')', ';');
  }

  private void generateWriteFieldForCollection(String jsonName, GenObject var, Type elementType) {
    dot(v.jsonGenerator, "writeArrayFieldStart").c('(').val(jsonName).c(')', ';');
    generateWriteCollectionLoop(var, elementType);
  }

  private void generateWriteCollectionLoop(GenObject var, Type type) {
    final GenObject element = v.elementNameVarMgr.nextName();
    s("for").sp().c('(').s("final").sp().t(type).sp().obj(element).spc(':').obj(var).c(')').sp().c('{');
    // {ElementType} element#N = element
    generateWriteObject(element, type);
    c('}'); // end of for
    v.elementNameVarMgr.releaseName(); // returned name is no longer needed, we're out of scope
    dot(v.jsonGenerator, "writeEndArray").c('(').c(')', ';');
  }

  private void generateWriteFieldForClass(String jsonName, GenObject var, Class<?> clazz) {
    Assert.state(!clazz.isArray(), "Array class for field");
    if (DefaultValues.NUMERIC_PRIMITIVES.contains(clazz) || BigDecimal.class.equals(clazz)) {
      dot(v.jsonGenerator, "writeNumberField").c('(').val(jsonName).c(',', ' ').obj(var).c(')', ';');
      return;
    }

    if (clazz.isPrimitive()) {
      throw new UnsupportedOperationException("Field " + clazz + " : " + jsonName + " can't be written as JSON");
    }

    if (String.class.equals(clazz)) {
      dot(v.jsonGenerator, "writeStringField").c('(').val(jsonName).c(',', ' ').obj(var).c(')', ';');
      return;
    }

    // write as object (assuming jackson can deal with it)
    generateWriteFieldAsObject(jsonName, var);
  }

  private void generateWriteArray(GenObject var, Type elementType) {
    dot(v.jsonGenerator, "writeArrayStart").c('(', ')', ';');
    generateWriteCollectionLoop(var, elementType);
  }

  private void generateWriteClass(GenObject var, Class<?> clazz) {
    if (DefaultValues.NUMERIC_PRIMITIVES.contains(clazz) || BigDecimal.class.equals(clazz) ||
        BigInteger.class.equals(clazz)) {
      dot(v.jsonGenerator, "writeNumber").c('(').obj(var).c(')', ';');
      return;
    }

    if (clazz.isPrimitive()) {
      throw new UnsupportedOperationException(clazz + " can't be written as JSON");
    }

    if (String.class.equals(clazz)) {
      dot(v.jsonGenerator, "writeString").c('(').obj(var).c(')', ';');
      return;
    }

    // write as object (assuming jackson can deal with it)
    dot(v.jsonGenerator, "writeObject").c('(').obj(var).c(')', ';');
  }

  private void generateWriteObject(final GenObject var, Type type) {
    TypeVisitor.apply(new TypeVisitor<Void>() {
      @Override
      public Void visitArray(@Nonnull Type sourceType, @Nonnull Type elementType) {
        generateWriteArray(var, elementType);
        return null;
      }

      @Override
      public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Type rawType, @Nonnull List<? extends Type> args) {
        throw new UnsupportedOperationException();
        //return null;
      }

      @Override
      public Void visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
        generateWriteClass(var, clazz);
        return null;
      }

      @Override
      public Void visitGenClass(@Nonnull Type sourceType, @Nonnull GenClass genClass) {
        // TODO: find static writer - avoid unnecessary dispatching by jackson
        // write as object (assuming jackson can deal with it)
        dot(v.jsonGenerator, "writeObject").c('(').obj(var).c(')', ';');
        return null;
      }
    }, type);
  }

  //
  // Deserializer (Reader)
  //

  private void generateDeserializerSupport(@Nonnull JsonTarget target) {
    if (!target.isReaderSupportRequested()) {
      return;
    }

    s("// Deserializer for class " + target.getDomainClass().getOrigin().getOriginClass()).eol();

    final Class<?> originDomainClass = target.getDomainAnalysisResult().getOriginClass();
    final GenClass readerClass = nonNull(target.getTargetReaderClass(), "Reader class is null");

    // public static final class {Deserializer} extends JsonDeserializer<{DomainClass}> {}
    s("public").sp().s("static").sp().s("final").sp();
    s("class").sp().s(readerClass.getFqName().getName()).sp();
    s("extends").sp().t(SynteticParameterizedType.from(T_JSON_DESERIALIZER, originDomainClass)).sp();
    c('{'); // class body begins

    // @Override public {DomainClass} deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    annotate(Override.class).s("public").sp().t(originDomainClass).sp().s("deserialize");
    c('(').t(T_JSON_PARSER).sp().s(v.jsonParser).commaSp().t(T_DESERIALIZATION_CONTEXT).sp().s(v.deserializationContext).c(')').sp();
    s("throws").sp().t(IOException.class).sp();
    c('{'); // start body
    generateReadMethodBody(target);
    c('}'); // end of deserialize method

    c('}'); // end of class body
  }

  private void generateReadMethodBody(@Nonnull JsonTarget target) {
    final Class<?> domainClass = target.getDomainAnalysisResult().getOriginClass();

    // if (jp.getCurrentToken() != JsonToken.START_OBJECT) ctxt.mappingException({DomainValue}.class);
    s("if").sp().c('(').dot(v.jsonParser, "getCurrentToken").c('(', ')').sps("!=").t(T_JSON_TOKEN).dot("START_OBJECT").c(')').sp();
    c('{');
    s("throw").sp().dot(v.deserializationContext, "mappingException").c('(').t(domainClass).dot("class").c(')', ';');
    c('}').eol();

    generateInitializerFields(target);
    generateReaderLoop(target);
    generateReturnDeserializedValue(target);
  }

  private void generateReturnDeserializedValue(@Nonnull JsonTarget target) {
    // the only one assumption here: the target object has respective class w/ public constructor
    // with all the parameters equivalent to the fields in the declared order
    final GenDomainClass genDomainClass = domainClassToJsonTarget.inverse().get(target);
    // TODO: remove temp stub -- return null
    s("return").sp().newType(genDomainClass).c('(');
    boolean next = false;
    for (final DomainField field : target.getDomainAnalysisResult().getFields()) {
      if (next) {
        nextParam(field.getFieldName());
      } else {
        s(field.getFieldName());
        next = true;
      }
    }
    c(')', ';');
  }

  private void generateInitializerFields(@Nonnull JsonTarget target) {
    for (final DomainField field : target.getDomainAnalysisResult().getFields()) {
      t(field.getFieldType()).sp().s(field.getFieldName()).spc('=');
      final Class<?> maybeClass = field.getFieldTypeAsClass();
      if (maybeClass != null && maybeClass.isPrimitive()) {
        if (boolean.class.equals(maybeClass)) {
          val(false); // boolean primitive
        } else {
          val(0); // primitive integer = 0
        }
      } else {
        s("null");
      }
      c(';');
    }
    eol();
  }

  private void generateReaderLoop(@Nonnull JsonTarget target) {
    final DomainAnalysisResult analysisResult = target.getDomainAnalysisResult();
    final List<DomainField> fields = analysisResult.getFields();
    if (fields.isEmpty()) {
      log.warn("{} has no deserializable fields", analysisResult.getOriginClass());
      return; // there is no point in reading any properties - object has no properties
    }

    // reader
    //    for (;;) {
    s("for").sp().c('(').s(";;").c(')').sp().c('{');
    // read field name or end-of-object
    //    final JsonToken token = jp.nextToken();
    s("final").sp().t(T_JSON_TOKEN).sp().s(v.token).spc('=').dot("jp", "nextToken").c('(', ')', ';');
    //    if (token == JsonToken.END_OBJECT) {
    //      break;
    //    } else if (token != JsonToken.FIELD_NAME) {
    s("if").sp().c('(').s(v.token).sps("==").t(T_JSON_TOKEN).dot("END_OBJECT").c(')').sp().c('{');
    s("break").c(';');
    c('}').s("else").sp().s("if").sp().c('(').s(v.token).sps("!=").t(T_JSON_TOKEN).dot("FIELD_NAME").c(')').sp().c('{');
    //      throw ctxt.mappingException("Field name expected");
    s("throw").sp().dot(v.deserializationContext, "mappingException").c('(').val("Field name expected").c(')', ';');
    //    }
    //    final String fieldName = jp.getCurrentName();
    c('}').s("final").sp().t(String.class).sp().s(v.fieldName).spc('=').dot(v.jsonParser, "getCurrentName").c('(', ')', ';');

    eol();

    //      jp.nextToken();
    dot(v.jsonParser, "nextToken").c('(', ')', ';');

    // read field value - matching field name against existing properties
    //      if ("{fieldName}".equals(fieldName)) {
    //        {fieldName} = jp.getIntValue();
    //      } else if ("lines".equals(fieldName)) {
    //        {fieldName} = jp.readValueAs(new TypeReference<List<String>>() {});
    boolean next = false;
    for (final DomainField field : fields) {
      if (next) {
        c('}').s("else").sp().s("if");
      } else {
        s("if");
        next = true;
      }

      sp().c('(').val(getJsonName(field)).dot("equals").c('(').s(v.fieldName).c(')', ')').sp().c('{');
      s(field.getFieldName()).spc('=');
      generateFieldAssignment(field);
      c(';');
    }
    //      } else {
    //        ctxt.handleUnknownProperty(jp, this, Memo.class, fieldName);
    //      }
    c('}').s("else").sp().c('{').dot(v.deserializationContext, "handleUnknownProperty").c('(');
    s(v.jsonParser).nextParam("this").commaSp().t(analysisResult.getOriginClass()).dot("class");
    nextParam(v.fieldName).c(')', ';', '}');

    c('}'); // end of reader for-loop
    eol();
  }

  private void generateFieldAssignment(@Nonnull DomainField field) {
    s(v.jsonParser);
    TypeVisitor.apply(new TypeVisitor<CodeStreamSupport>() { // parameterized w/ CodeStreamSupport to simplify implementation
      @Override
      public CodeStreamSupport visitType(@Nonnull Type sourceType) {
        //    jp.readValueAs(new TypeReference<{ComplexOrParameterizedType}>() {})
        dot("readValueAs").c('(').newType(SynteticParameterizedType.from(T_TYPE_REFERENCE, sourceType));
        return c('(', ')').sp().s("{}").c(')');
      }

      @Override
      public CodeStreamSupport visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
        // primitive types case
        if (clazz.isPrimitive()) {
          // TODO: no support for char type at the moment, fallback to object case
          if (boolean.class.equals(clazz)) {
            return dot("getBooleanValue").c('(', ')');
          } else if (byte.class.equals(clazz)) {
            return dot("getByteValue").c('(', ')');
          } else if (short.class.equals(clazz)) {
            return dot("getShortValue").c('(', ')');
          } else if (int.class.equals(clazz)) {
            return dot("getIntValue").c('(', ')');
          } else if (float.class.equals(clazz)) {
            return dot("getFloatValue").c('(', ')');
          } else if (double.class.equals(clazz)) {
            return dot("getDoubleValue").c('(', ')');
          } else if (long.class.equals(clazz)) {
            return dot("getLongValue").c('(', ')');
          }
        } else if (String.class.equals(clazz)) {
          return dot("getValueAsString").c('(', ')');
        } else if (BigInteger.class.equals(clazz)) {
          return dot("getBigIntegerValue").c('(', ')');
        } else if (BigDecimal.class.equals(clazz)) {
          return dot("getDecimalValue").c('(', ')');
        } else if (Number.class.equals(clazz)) {
          return dot("getNumberValue").c('(', ')');
        }

        // arbitrary object case
        //    jp.readValueAs({Class}.class)
        return dot("readValueAs").c('(').t(clazz).dot("class").c(')');
      }

      @Override
      public CodeStreamSupport visitArray(@Nonnull Type sourceType, @Nonnull Type elementType) {
        //    jp.readValueAs({Class[]}.class)
        return dot("readValueAs").c('(').t(elementType).dot("class").c(')');
      }
    }, field.getFieldType());
  }
}
