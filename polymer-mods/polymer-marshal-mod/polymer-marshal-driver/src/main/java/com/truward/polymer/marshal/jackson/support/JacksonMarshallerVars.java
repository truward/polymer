package com.truward.polymer.marshal.jackson.support;

import com.truward.polymer.core.code.builder.CodeFactory;
import com.truward.polymer.core.util.VarNameManager;
import com.truward.polymer.domain.analysis.support.Names;

import javax.annotation.Nonnull;

/**
 * Class, for managing jackson marshaller's variables
 *
 * @author Alexander Shabanov
 */
public final class JacksonMarshallerVars {
  public final String jsonGenerator;
  public final String jsonParser;
  public final String value = Names.VALUE;
  public final String writeBody; // writeBody method name
  public final String attachMarshallersTo;
  public final String mapper;
  public final String module;
  public final String deserializationContext;
  public final String token;
  public final String fieldName;
  public final VarNameManager elementNameVarMgr;

  public JacksonMarshallerVars(@Nonnull CodeFactory codeFactory) {
    this.jsonGenerator = "jg"; // jg = json generator
    this.jsonParser = "jp"; // jp = json parser
    this.writeBody = "writeBody";
    this.attachMarshallersTo = "attachMarshallersTo";
    this.mapper = "mapper";
    this.module = "module";
    this.deserializationContext = "ctxt";
    this.token = "token";
    this.fieldName = "fieldName";
    this.elementNameVarMgr = new VarNameManager(Names.ELEMENT, codeFactory);
  }
}
