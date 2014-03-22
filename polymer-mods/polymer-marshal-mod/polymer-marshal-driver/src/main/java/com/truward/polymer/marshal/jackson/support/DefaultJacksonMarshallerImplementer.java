package com.truward.polymer.marshal.jackson.support;

import com.truward.polymer.core.code.builder.CodeStream;
import com.truward.polymer.core.code.builder.CodeStreamSupport;
import com.truward.polymer.core.code.builder.ModuleBuilder;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.printer.CodePrinter;
import com.truward.polymer.core.code.typed.*;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.core.output.DefaultFileTypes;
import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.core.support.code.DefaultModuleBuilder;
import com.truward.polymer.core.support.code.DefaultTypeManager;
import com.truward.polymer.core.support.code.printer.DefaultCodePrinter;
import com.truward.polymer.core.types.DefaultValues;
import com.truward.polymer.core.types.SynteticParameterizedType;
import com.truward.polymer.core.util.Assert;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.FieldUtil;
import com.truward.polymer.domain.analysis.OriginMethodRole;
import com.truward.polymer.domain.analysis.support.GenDomainClass;
import com.truward.polymer.domain.analysis.support.Names;
import com.truward.polymer.marshal.json.analysis.JsonMarshallerImplementer;
import com.truward.polymer.marshal.json.analysis.JsonTarget;
import com.truward.polymer.marshal.json.support.analysis.DefaultJsonTarget;
import com.truward.polymer.naming.FqName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public class DefaultJacksonMarshallerImplementer extends FreezableSupport
    implements JsonMarshallerImplementer, SpecificationStateAware {
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final Map<GenDomainClass, JsonTarget> domainClassToJsonTarget = new HashMap<>();
  private final Map<JsonTarget, GenClass> serializerClasses = new HashMap<>();
  private final Map<JsonTarget, GenClass> deserializerClasses = new HashMap<>();

  @Resource
  private OutputStreamProvider outputStreamProvider;

  private FqName targetClassName;
  private boolean mappersRequired = true; // TODO: expose as a property

  @Override
  public void submit(@Nonnull GenDomainClass domainClass) {
    checkNonFrozen();

    if (domainClassToJsonTarget.containsKey(domainClass)) {
      log.info("Duplicate submission of domain class {}", domainClass);
      return;
    }

    domainClassToJsonTarget.put(domainClass, new DefaultJsonTarget(domainClass));
  }

  @Override
  public void generateImplementations() {
    try {
      log.info("Generating file for {}", targetClassName);

      // prepare module generator
      final TypeManager typeManager = new DefaultTypeManager();
      final ModuleBuilder moduleBuilder = new DefaultModuleBuilder(targetClassName, typeManager);

      // generate code
      final JsonBinderImplementer binderImplementer = new JsonBinderImplementer(targetClassName,
          moduleBuilder.getStream());
      binderImplementer.generate();

      // freeze generated code
      moduleBuilder.freeze();

      // dump code to the file
      try (final OutputStream stream = outputStreamProvider.createStreamForFile(targetClassName, DefaultFileTypes.JAVA)) {
        try (final OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
          final CodePrinter codePrinter = new DefaultCodePrinter(writer, typeManager);
          codePrinter.print(moduleBuilder.getStream());
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    log.info("Done with Jackson marshallers generation");
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    if (state == SpecificationState.COMPLETED) {
      if (targetClassName == null) {
        // TODO: exception?
        targetClassName = FqName.parse("generated.JsonMarshaller");
      }

      checkNonFrozen();

      finalizeAnalysis();
      freeze();
    }
  }

  //
  // Private
  //

  private void finalizeAnalysis() {
    Assert.nonNull(targetClassName, "Target class name expected to be non-null");

    if (mappersRequired) {
      for (final JsonTarget target : domainClassToJsonTarget.values()) {
        final String simpleName = target.getDomainClass().getOrigin().getOriginClass().getSimpleName();

        if (target.isReaderSupportRequested() && !deserializerClasses.containsKey(target)) {
          deserializerClasses.put(target, GenClassReference.from(new FqName(simpleName + "Deserializer", targetClassName)));
        }

        if (target.isWriterSupportRequested() && !serializerClasses.containsKey(target)) {
          serializerClasses.put(target, GenClassReference.from(new FqName(simpleName + "Serializer", targetClassName)));
        }
      }
    }

    log.info("Json marshaller analysis has been completed");
  }

  // jackson serializer classes
  private static final GenClass T_JSON_PARSER = GenClassReference.from("com.fasterxml.jackson.core.JsonParser");
  private static final GenClass T_JSON_GENERATOR = GenClassReference.from("com.fasterxml.jackson.core.JsonGenerator");

  private final class JsonBinderImplementer extends CodeStreamSupport {
    private final CodeStream codeStream;
    private final FqName fqName;

    private final String out = "jg"; // jg = json generator
    private final String in = "jp"; // jp = json parser
    private final String value = Names.VALUE;

    private JsonBinderImplementer(@Nonnull FqName fqName, @Nonnull CodeStream codeStream) {
      this.fqName = fqName;
      this.codeStream = codeStream;
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
      s("// type adapter " + target.getDomainClass().getOrigin().getOriginClass()).eol();

      final Class<?> originDomainClass = target.getDomainAnalysisResult().getOriginClass();
      //final GenClass domainClass = target.getDomainClass();

      if (target.isWriterSupportRequested()) {
        // public static void write(JsonGenerator out, {DomainClass} value) throws IOException {...}
        s("public").sp().s("static").sp().t(void.class).sp().s("write").c('(');
        var(T_JSON_GENERATOR, out).c(',').sp().var(originDomainClass, value);
        c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
        generateWriteMethodBody(out, value, target);
        c('}').eol(); // End of write(JsonWriter out, {DomainClass} value)
      }

      if (target.isReaderSupportRequested()) {
        // public static {DomainClass} read(JsonReader in, Class<{DomainClass}> dummy) throws IOException {...}
        s("public").sp().s("static").sp().t(originDomainClass).sp().s("read").c('(');
        var(T_JSON_PARSER, in).c(',').sp().var(SynteticParameterizedType.from(Class.class, originDomainClass), "dummy");
        c(')').sp().s("throws").sp().t(IOException.class).sp().c('{');
        generateReadMethodBody();
        c('}'); // End of read(JsonParser in, Class<{DomainClass}> dummy)
      }
    }

    private void generateWriteMethodBody(String out, String value, JsonTarget target) {
      dot(out, "writeStartObject").c('(', ')', ';');
      for (final DomainField field : target.getDomainAnalysisResult().getFields()) {
        final String getterName = FieldUtil.getMethodName(field, OriginMethodRole.GETTER);
        if (getterName == null) {
          throw new UnsupportedOperationException("Can't generate writeObject: field " + field +
              " has no associated getter");
        }

        generateFieldEntry(getterName, field);
      }
      dot(out, "writeEndObject").c('(', ')', ';');
    }

    private String getJsonName(DomainField field) {
      return field.getFieldName();
    }

    private void generateFieldEntry(final String getterName, final DomainField field) {
      TypeVisitor.apply(new TypeVisitor<Void>() {
        @Override
        public Void visitType(@Nonnull Type sourceType) {
          throw new UnsupportedOperationException("Unknown type?");
        }

        @Override
        public Void visitArray(@Nonnull Type sourceType, @Nonnull Type elementType) {
          throw new UnsupportedOperationException();
        }

        @Override
        public Void visitClass(@Nonnull Type sourceType, @Nonnull Class<?> clazz) {
          generateWriteFieldForClass(field, getterName, clazz);
          return null;
        }

        @Override
        public Void visitGenClass(@Nonnull Type sourceType, @Nonnull GenClass genClass) {
          dot(out, "writeObjectField").c('(').val(getJsonName(field)).c(',', ' ').callGetter(value, getterName).c(')', ';');
          return null;
        }

        @Override
        public Void visitGenericType(@Nonnull Type sourceType, @Nonnull Type rawType, @Nonnull List<? extends Type> args) {
          throw new UnsupportedOperationException();
        }
      }, field.getFieldType());
    }

    private void generateWriteFieldForCollection(DomainField field, String getterName, Class<?> elementClazz) {
      final String element = Names.ELEMENT;
      dot(out, "writeArrayFieldStart").c('(').val(getJsonName(field)).c(')', ';');
      s("for").sp().c('(').s(element).spc(':').callGetter(value, getterName).c(')').sp().c('{');
      generateWriteForClass(getterName, elementClazz);
      c('}'); // end of for
      dot(out, "writeEndArray").c('(').c(')', ';');
    }

    private void generateWriteFieldForClass(DomainField field, String getterName, Class<?> clazz) {
      if (DefaultValues.NUMERIC_PRIMITIVES.contains(clazz)) {
        dot(out, "writeNumberField").c('(').val(getJsonName(field)).c(',', ' ').callGetter(value, getterName).c(')', ';');
        return;
      }

      if (clazz.isPrimitive()) {
        throw new UnsupportedOperationException("Field " + field + " can't be written as JSON");
      }

      if (String.class.equals(clazz)) {
        dot(out, "writeStringField").c('(').val(getJsonName(field)).c(',', ' ').callGetter(value, getterName).c(')', ';');
        return;
      }

      // write as object (assuming jackson can deal with it)
      dot(out, "writeObjectField").c('(').val(getJsonName(field)).c(',', ' ').callGetter(value, getterName).c(')', ';');
    }

    private void generateWriteForClass(String getterName, Class<?> clazz) {
      if (DefaultValues.NUMERIC_PRIMITIVES.contains(clazz)) {
        dot(out, "writeNumber").c('(').callGetter(value, getterName).c(')', ';');
        return;
      }

      if (clazz.isPrimitive()) {
        throw new UnsupportedOperationException("Class " + clazz + " can't be written as JSON");
      }

      if (String.class.equals(clazz)) {
        dot(out, "writeString").c('(').callGetter(value, getterName).c(')', ';');
        return;
      }

      // write as object (assuming jackson can deal with it)
      dot(out, "writeObject").c('(').callGetter(value, getterName).c(')', ';');
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

    private void generateReadMethodBody() {
      throwUnsupportedOperationException();
    }
  }
}
