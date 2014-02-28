package com.truward.polymer.core.support.code;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.truward.polymer.core.code.CodeObject;
import com.truward.polymer.core.code.builder.CodeStream;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.untyped.GenChar;
import com.truward.polymer.core.code.untyped.GenFqNamed;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.code.untyped.GenString;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Shabanov
 */
public final class DefaultInlineBlock extends FreezableSupport implements GenInlineBlock {
  private List<CodeObject> childs = new ArrayList<>(30);
  private final TypeManager typeManager;
  private static final Map<String, GenStringImpl> STRING_CACHE;

  static {
    final Map<String, GenStringImpl> cache = new HashMap<>();
    addToCache(cache, "public");
    addToCache(cache, "private");
    addToCache(cache, "protected");
    addToCache(cache, "package");
    addToCache(cache, "import");
    addToCache(cache, "class");
    addToCache(cache, "static");
    addToCache(cache, "final");
    addToCache(cache, "volatile");
    addToCache(cache, "transient");
    addToCache(cache, "true");
    addToCache(cache, "false");
    addToCache(cache, "null");
    addToCache(cache, "extends");
    addToCache(cache, "implements");
    addToCache(cache, "new");
    addToCache(cache, "this");
    addToCache(cache, "super");

    STRING_CACHE = ImmutableMap.copyOf(cache);
  }

  private static void addToCache(Map<String, GenStringImpl> cache, String value) {
    cache.put(value, new GenStringImpl(value));
  }

  public DefaultInlineBlock(@Nonnull TypeManager typeManager) {
    this.typeManager = typeManager;
  }

  @Nonnull
  @Override
  public CodeStream c(char ch) {
    return append(GenCharImpl.valueOf(ch));
  }

  @Nonnull
  @Override
  public CodeStream c(@Nonnull char... chars) {
    for (final char ch : chars) {
      c(ch);
    }
    return this;
  }

  @Nonnull
  @Override
  public CodeStream s(@Nonnull String string) {
    GenStringImpl gs = STRING_CACHE.get(string);
    if (gs == null) {
      gs = new GenStringImpl(string);
    }
    return append(gs);
  }

  @Nonnull
  @Override
  public CodeStream s(@Nonnull String... strings) {
    for (final String string : strings) {
      s(string);
    }
    return this;
  }

  @Nonnull
  @Override
  public CodeStream s(@Nonnull FqName fqName) {
    return append(new GenFqNamedImpl(fqName));
  }

  @Nonnull
  @Override
  public CodeStream sp() {
    return c(' ');
  }

  @Nonnull
  @Override
  public CodeStream eol() {
    return c('\n');
  }

  @Nonnull
  @Override
  public CodeStream t(@Nonnull Type type) {
    return append(typeManager.adaptType(type));
  }

  @Nonnull
  @Override
  public List<CodeObject> getChilds() {
    checkNonFrozen();
    return childs;
  }

  @Override
  protected void setFrozen() {
    childs = ImmutableList.copyOf(childs);
    super.setFrozen();
  }

  //
  // Private
  //
  
  @Nonnull
  private CodeStream append(@Nonnull CodeObject codeObject) {
    checkNonFrozen();
    childs.add(codeObject);
    return this;
  }

  //
  // Private implementations
  //

  private static final class GenCharImpl implements GenChar {
    private static final GenCharImpl[] CHAR_CACHE = new GenCharImpl[128];

    static {
      for (int i = 0; i < CHAR_CACHE.length; ++i) {
        CHAR_CACHE[i] = new GenCharImpl((char) i);
      }
    }

    private final char ch;

    private GenCharImpl(char ch) {
      this.ch = ch;
    }

    @Override
    public char getChar() {
      return ch;
    }

    @Nonnull
    public static GenChar valueOf(char ch) {
      if (ch >= 0 && ch < CHAR_CACHE.length) {
        return CHAR_CACHE[ch];
      }
      return new GenCharImpl(ch);
    }
  }

  private static final class GenStringImpl implements GenString {
    private final String value;

    private GenStringImpl(String value) {
      this.value = value;
    }

    @Override
    public String getString() {
      return value;
    }
  }

  private static final class GenFqNamedImpl implements GenFqNamed {
    private final FqName fqName;

    private GenFqNamedImpl(@Nonnull FqName fqName) {
      this.fqName = fqName;
    }

    @Override
    public void setFqName(@Nonnull FqName name) {
      throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public FqName getFqName() {
      return fqName;
    }
  }
}
