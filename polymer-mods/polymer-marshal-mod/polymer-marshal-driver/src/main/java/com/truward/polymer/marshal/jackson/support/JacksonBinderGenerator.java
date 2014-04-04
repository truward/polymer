package com.truward.polymer.marshal.jackson.support;

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
import com.truward.polymer.core.util.Assert;
import com.truward.polymer.core.util.VarNameManager;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.FieldUtil;
import com.truward.polymer.domain.analysis.OriginMethodRole;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.domain.analysis.support.Names;
import com.truward.polymer.marshal.json.analysis.JsonFieldRegistry;
import com.truward.polymer.marshal.json.analysis.JsonTarget;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.truward.polymer.core.util.Assert.nonNull;

/**
 * Contains implementer of the jackson serialization/deserialization code.
 *
 * @author Alexander Shabanov
 */
final class JacksonBinderGenerator extends CodeStreamSupport {
  // jackson serializer classes
  private static final GenClass T_JSON_PARSER = GenClassReference.from("com.fasterxml.jackson.core.JsonParser");
  private static final GenClass T_JSON_GENERATOR = GenClassReference.from("com.fasterxml.jackson.core.JsonGenerator");
  private static final GenClass T_JSON_SERIALIZER = GenClassReference.from("com.fasterxml.jackson.databind.JsonSerializer");

  private final Map<GenDomainClass, JsonTarget> domainClassToJsonTarget;
  private final CodeStream codeStream;
  private final FqName fqName;

  private final String out;
  private final String in;
  private final String value = Names.VALUE;
  private final String writeBody; // writeBody method name
  private final VarNameManager elementNameVarMgr = new VarNameManager(Names.ELEMENT, this);

  private final JsonFieldRegistry fieldRegistry;

  public JacksonBinderGenerator(@Nonnull FqName fqName, @Nonnull CodeStream codeStream,
                                @Nonnull Map<GenDomainClass, JsonTarget> domainClassToJsonTarget,
                                @Nonnull JsonFieldRegistry fieldRegistry) {
    this.fqName = fqName;
    this.codeStream = codeStream;
    this.domainClassToJsonTarget = domainClassToJsonTarget;
    this.fieldRegistry = fieldRegistry;

    this.out = "jg"; // jg = json generator
    this.in = "jp"; // jp = json parser
    this.writeBody = "writeBody";
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
      generateStaticMarshallers(target);
    }

    c('}').eol(); // class body end
  }

  private void generateStaticMarshallers(@Nonnull JsonTarget target) {
    generateWriterSupport(target);
    generateReaderSupport(target);
  }

  @Nonnull
  private String getJsonName(@Nonnull DomainField field) {
    return fieldRegistry.getJsonName(field);
  }


  //
  // Serializer (Writer)
  //

  private void generateWriterSupport(@Nonnull JsonTarget target) {
    if (!target.isWriterSupportRequested()) {
      return;
    }

    s("// Writer for class " + target.getDomainClass().getOrigin().getOriginClass()).eol();

    final Class<?> originDomainClass = target.getDomainAnalysisResult().getOriginClass();
    final GenClass writerClass = nonNull(target.getTargetWriterClass(), "Writer class is null");

    // public static final class {Writer} extends JsonSerializer<Memo> {}
    s("public").sp().s("static").sp().s("final").sp();
    s("class").sp().s(writerClass.getFqName().getName()).sp();
    s("extends").sp().t(SynteticParameterizedType.from(T_JSON_SERIALIZER, originDomainClass)).sp();
    // class body
    c('{');

    // @Override public void serialize({DomainType} value, JsonGenerator out, SerializerProvider provider) throws IOException
    annotate(Override.class).s("public").sp().t(void.class).sp().s("serialize");
    c('(').c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
    dot(out, "writeStartObject").c('(', ')', ';');
    s("writeBody").c('(').s(out).c(',').sp().s(value).c(')', ';');
    dot(out, "writeEndObject").c('(', ')', ';');
    c('}'); // end of serialize method

    // public static void writeBody(JsonGenerator out, {DomainClass} value) throws IOException {...}
    s("public").sp().s("static").sp().t(void.class).sp().s(writeBody).c('(');
    var(T_JSON_GENERATOR, out).c(',').sp().var(originDomainClass, value);
    c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
    generateWriteMethodBody(target);
    c('}').eol(); // End of writeBody(JsonWriter out, {DomainClass} value)

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
    new SimpleDelegatingCodeStream(getterCall).callGetter(value, getterName);
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
    dot(out, "writeObjectField").c('(').val(jsonName).c(',', ' ').obj(var).c(')', ';');
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
    dot(out, "writeObjectField").c('(').val(jsonName).c(',', ' ').obj(var).c(')', ';');
  }

  private void generateWriteFieldForCollection(String jsonName, GenObject var, Type elementType) {
    dot(out, "writeArrayFieldStart").c('(').val(jsonName).c(')', ';');
    generateWriteCollectionLoop(var, elementType);
  }

  private void generateWriteCollectionLoop(GenObject var, Type type) {
    final GenObject element = elementNameVarMgr.nextName();
    s("for").sp().c('(').s("final").sp().t(type).sp().obj(element).spc(':').obj(var).c(')').sp().c('{');
    // {ElementType} element#N = element
    generateWriteObject(element, type);
    c('}'); // end of for
    elementNameVarMgr.releaseName(); // returned name is no longer needed, we're out of scope
    dot(out, "writeEndArray").c('(').c(')', ';');
  }

  private void generateWriteFieldForClass(String jsonName, GenObject var, Class<?> clazz) {
    Assert.state(!clazz.isArray(), "Array class for field");
    if (DefaultValues.NUMERIC_PRIMITIVES.contains(clazz) || BigDecimal.class.equals(clazz)) {
      dot(out, "writeNumberField").c('(').val(jsonName).c(',', ' ').obj(var).c(')', ';');
      return;
    }

    if (clazz.isPrimitive()) {
      throw new UnsupportedOperationException("Field " + clazz + " : " + jsonName + " can't be written as JSON");
    }

    if (String.class.equals(clazz)) {
      dot(out, "writeStringField").c('(').val(jsonName).c(',', ' ').obj(var).c(')', ';');
      return;
    }

    // write as object (assuming jackson can deal with it)
    generateWriteFieldAsObject(jsonName, var);
  }

  private void generateWriteArray(GenObject var, Type elementType) {
    dot(out, "writeArrayStart").c('(', ')', ';');
    generateWriteCollectionLoop(var, elementType);
  }

  private void generateWriteClass(GenObject var, Class<?> clazz) {
    if (DefaultValues.NUMERIC_PRIMITIVES.contains(clazz) || BigDecimal.class.equals(clazz) ||
        BigInteger.class.equals(clazz)) {
      dot(out, "writeNumber").c('(').obj(var).c(')', ';');
      return;
    }

    if (clazz.isPrimitive()) {
      throw new UnsupportedOperationException("Class " + clazz + " can't be written as JSON");
    }

    if (String.class.equals(clazz)) {
      dot(out, "writeString").c('(').obj(var).c(')', ';');
      return;
    }

    // write as object (assuming jackson can deal with it)
    dot(out, "writeObject").c('(').obj(var).c(')', ';');
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
        dot(out, "writeObject").c('(').obj(var).c(')', ';');
        return null;
      }
    }, type);
  }

  //
  // Deserializer (Reader)
  //

  private void generateReaderSupport(@Nonnull JsonTarget target) {
    if (!target.isReaderSupportRequested()) {
      return;
    }

    s("// Reader for class " + target.getDomainClass().getOrigin().getOriginClass()).eol();

    final Class<?> originDomainClass = target.getDomainAnalysisResult().getOriginClass();
    final GenClass readerClass = nonNull(target.getTargetReaderClass(), "Reader class is null");

    // public static {DomainClass} read(JsonReader in, Class<{DomainClass}> dummy) throws IOException {...}
    s("public").sp().s("static").sp().t(originDomainClass).sp().s("read").c('(');
    var(T_JSON_PARSER, in).c(',').sp().var(SynteticParameterizedType.from(Class.class, originDomainClass), "dummy");
    c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
    generateReadMethodBody();
    c('}'); // End of read(JsonParser in, Class<{DomainClass}> dummy)
  }

  private void generateReadMethodBody() {
    throwUnsupportedOperationException();
  }
}
