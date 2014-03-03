package com.truward.polymer.core.code.builder;

import com.google.common.collect.ImmutableMap;
import com.truward.polymer.core.code.GenObject;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class, that provides a code stream support for implementers.
 * Besides the methods, provided in {@link CodeStream} provides another ones for convenience purposes,
 * that extend functionality of the basic {@link CodeStream}.
 *
 * @author Alexander Shabanov
 */
public abstract class CodeStreamSupport implements CodeStream {
  private static final Map<Modifier, String> MODIFIER_STRING_MAP;

  static {
    final Modifier[] values = Modifier.values();
    final Map<Modifier, String> modifiers = new HashMap<>(values.length);
    for (final Modifier modifier : values) {
      modifiers.put(modifier, modifier.toString());
    }
    MODIFIER_STRING_MAP = ImmutableMap.copyOf(modifiers);
  }

  @Nonnull
  protected abstract CodeStream getRootCodeStream();

  public final CodeStreamSupport modifiers(@Nonnull List<Modifier> modifiers) {
    for (final Modifier modifier : modifiers) {
      s(MODIFIER_STRING_MAP.get(modifier)).sp();
    }
    return this;
  }

  public final CodeStreamSupport var(@Nonnull Type type, @Nonnull String name) {
    return t(type).sp().s(name);
  }

  public final CodeStreamSupport var(@Nonnull Type type, @Nonnull String name, @Nonnull List<Modifier> mods) {
    return modifiers(mods).var(type, name);
  }

  //=> .{name}
  @Nonnull
  public final CodeStreamSupport dot(@Nonnull String name) {
    return c('.').s(name);
  }

  //=> {base}.{member}
  @Nonnull
  public final CodeStreamSupport dot(@Nonnull String base, @Nonnull String member) {
    return s(base).dot(member);
  }

  //=> this.{member}
  @Nonnull
  public final CodeStreamSupport thisDot(@Nonnull String name) {
    return dot("this", name);
  }

  //=> new {type}
  @Nonnull
  public final CodeStreamSupport newType(@Nonnull Type type) {
    return s("new").sp().t(type);
  }

  //=> ({type})
  @Nonnull
  public final CodeStreamSupport cast(@Nonnull Type type) {
    return c('(').t(type).c(')');
  }

  //=> _{c}_ <== _ is a space
  @Nonnull
  public final CodeStreamSupport spc(char c) {
    return sp().c(c).sp();
  }

  //=> _{s}_ <== _ is a space
  @Nonnull
  public final CodeStreamSupport sps(@Nonnull String s) {
    return sp().s(s).sp();
  }

  //=> @{type}\n
  @Nonnull
  public final CodeStreamSupport annotate(@Nonnull Type type) {
    return c('@').t(type).eol();
  }

  //=> throw new UnsupportedOperationException();
  @Nonnull
  public final CodeStreamSupport throwUnsupportedOperationException() {
    return s("throw").sp().newType(UnsupportedOperationException.class).c('(', ')', ';');
  }

  //=> public_final_class_
  @Nonnull
  public final CodeStreamSupport publicFinalClass() {
    return s("public").sp().s("final").sp().s("class").sp();
  }

  //=> public_static_final_class_
  @Nonnull
  public final CodeStreamSupport publicStaticFinalClass() {
    return s("public").sp().s("static").sp().s("final").sp().s("class").sp();
  }

  //
  // Overriden methods
  //
  
  @Nonnull
  @Override
  public CodeStreamSupport c(char ch) {
    getRootCodeStream().c(ch);
    return this;
  }

  @Nonnull
  @Override
  public CodeStreamSupport c(@Nonnull char... chars) {
    getRootCodeStream().c(chars);
    return this;
  }

  @Nonnull
  @Override
  public CodeStreamSupport s(@Nonnull String string) {
    getRootCodeStream().s(string);
    return this;
  }

  @Nonnull
  @Override
  public CodeStreamSupport s(@Nonnull String... strings) {
    getRootCodeStream().s(strings);
    return this;
  }

  @Nonnull
  @Override
  public CodeStreamSupport s(@Nonnull FqName fqName) {
    getRootCodeStream().s(fqName);
    return this;
  }

  @Nonnull
  @Override
  public CodeStreamSupport sp() {
    getRootCodeStream().sp();
    return this;
  }

  @Nonnull
  @Override
  public CodeStreamSupport eol() {
    getRootCodeStream().eol();
    return this;
  }

  @Nonnull
  @Override
  public CodeStreamSupport t(@Nonnull Type type) {
    getRootCodeStream().t(type);
    return this;
  }

  @Nonnull
  @Override
  public CodeStreamSupport obj(@Nonnull GenObject obj) {
    getRootCodeStream().obj(obj);
    return this;
  }

  @Nonnull
  @Override
  public List<GenObject> getChilds() {
    return getRootCodeStream().getChilds();
  }

  @Override
  public void freeze() {
    getRootCodeStream().freeze();
  }
}
