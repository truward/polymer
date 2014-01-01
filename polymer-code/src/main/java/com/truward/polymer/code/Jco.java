package com.truward.polymer.code;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.code.freezable.EmptyFreezable;
import com.truward.polymer.code.freezable.Freezable;
import com.truward.polymer.code.freezable.FreezableSupport;
import com.truward.polymer.code.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Class holder, that contains java code objects.
 * TODO: 'freezable' will probably complicate the overall design - remove?
 *
 * @author Alexander Shabanov
 */
public final class Jco {
  /** Hidden ctor */
  private Jco() {}


  public interface Node extends Freezable {
  }

  /**
   * Generic expression
   */
  public interface Expr extends Node {
  }

  public static final class CharExpr extends EmptyFreezable implements Expr {
    private final char ch;
    private static final CharExpr[] CACHED = new CharExpr[128];

    static {
      for (int i = 0; i < CACHED.length; ++i) {
        CACHED[i] = new CharExpr((char) i);
      }
    }

    public CharExpr(char ch) {
      this.ch = ch;
    }

    public static CharExpr valueOf(char ch) {
      if (ch >= 0 && ch < CACHED.length) {
        return CACHED[ch];
      }

      return new CharExpr(ch);
    }

    public char getCharacter() {
      return ch;
    }
  }

  public static final class Text extends EmptyFreezable implements Expr {
    private final String text;

    public Text(@Nonnull String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }
  }

  public static final class Value extends EmptyFreezable implements Expr {
    private final Object value;

    public Value(Object value) {
      this.value = value;
    }

    public Object getValue() {
      return value;
    }
  }

  public interface TypeExpr extends Node {
  }

  public interface GenericArg extends Node {
  }

  // Generic type variable - e.g. '?' or 'T' in List<?> or List<T>
  public interface GenericTypeVar extends Node {
  }

  public interface ClassRef extends TypeExpr, GenericArg {
  }

  public static final class ClassDeclRef extends EmptyFreezable implements ClassRef {
    private final ClassDecl classDecl;

    public ClassDeclRef(@Nonnull ClassDecl classDecl) {
      this.classDecl = classDecl;
    }

    @Nonnull
    public ClassDecl getClassDecl() {
      return classDecl;
    }
  }

  public static final class JavaClassRef extends EmptyFreezable implements ClassRef {
    private final Class<?> javaClass;

    public JavaClassRef(@Nonnull Class<?> javaClass) {
      this.javaClass = javaClass;
    }

    @Nonnull
    public Class<?> getJavaClass() {
      return javaClass;
    }
  }

  public static final class GenericTypeExpr extends EmptyFreezable implements TypeExpr {
    private final ClassRef classRef;
    private final List<GenericArg> args;

    public GenericTypeExpr(@Nonnull ClassRef classRef, @Nonnull List<GenericArg> args) {
      this.classRef = classRef;
      this.args = ImmutableList.copyOf(args);
    }

    @Nonnull
    public ClassRef getClassRef() {
      return classRef;
    }

    @Nonnull
    public List<GenericArg> getArgs() {
      return args;
    }
  }

  public static final class Wildcard extends EmptyFreezable implements GenericArg, GenericTypeVar {
    private static final Wildcard INSTANCE = new Wildcard();

    private Wildcard() {}

    public static Wildcard getInstance() {
      return INSTANCE;
    }
  }

  public static final class TypeParam extends EmptyFreezable implements GenericArg, GenericTypeVar {
    private final String name;

    public TypeParam(@Nonnull String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public static final class GenericArgExpr extends EmptyFreezable implements GenericArg {
    public static enum Kind {
      EXTENDS,
      IMPLEMENTS
    }

    private final GenericTypeVar typeVar; // e.g. ? or T
    private final Kind kind;              // e.g. extends or implements
    private final ClassRef classRef;      // e.g. Number

    public GenericArgExpr(@Nonnull GenericTypeVar typeVar, @Nonnull Kind kind, @Nonnull ClassRef classRef) {
      this.typeVar = typeVar;
      this.kind = kind;
      this.classRef = classRef;
    }

    public GenericTypeVar getTypeVar() {
      return typeVar;
    }

    public Kind getKind() {
      return kind;
    }

    public ClassRef getClassRef() {
      return classRef;
    }
  }

  public static final class Comment extends FreezableSupport implements Node {
    private List<Expr> exprs = new ArrayList<>();
    private boolean multiline;

    @Nonnull
    public List<Expr> getExprs() {
      return ImmutableList.copyOf(exprs);
    }

    public void addExpr(@Nonnull Expr expr) {
      exprs.add(expr);
    }

    public void addExprs(@Nonnull List<Expr> exprs) {
      exprs.addAll(exprs);
    }

    public boolean isMultiline() {
      return multiline;
    }

    public void setMultiline(boolean multiline) {
      checkNonFrozen();
      this.multiline = multiline;
    }

    @Override
    public void freeze() {
      exprs = ImmutableList.copyOf(exprs);
    }
  }


  public static final class Import {
    private final FqName qualifier;
    private final boolean isStatic;

    public Import(@Nonnull FqName qualifier, boolean isStatic) {
      this.qualifier = qualifier;
      this.isStatic = isStatic;
    }

    @Nonnull
    public FqName getQualifier() {
      return qualifier;
    }

    public boolean isStatic() {
      return isStatic;
    }
  }


  public interface Modifier {
    String getName();
  }

  public enum DefaultModifiers implements Modifier {
    PUBLIC("public"),
    PRIVATE("public"),
    STATIC("static"),
    FINAL("final");

    @Nonnull
    private final String name;

    @Nonnull
    public String getName() {
      return name;
    }

    private DefaultModifiers(@Nonnull String name) {
      this.name = name;
    }
  }

  public static final class Annotation extends FreezableSupport implements Expr {
    private TypeExpr annotationType;
    private List<Expr> parameters = new ArrayList<>();

    @Nonnull
    public TypeExpr getAnnotationType() {
      if (annotationType == null) {
        throw new IllegalStateException("Annotation type is not yet assigned");
      }
      return annotationType;
    }

    public Annotation setAnnotationType(@Nonnull TypeExpr annotationType) {
      this.annotationType = annotationType;
      return this;
    }

    public List<Expr> getParameters() {
      return ImmutableList.copyOf(parameters);
    }

    public void addParameter(@Nonnull Expr expr) {
      parameters.add(expr);
    }

    @Override
    public void freeze() {
      cannotBeFrozenIf(annotationType == null, "Annotation type has not been provided");
      super.freeze();
      parameters = ImmutableList.copyOf(parameters);
    }
  }

  public interface Annotated extends Node {
    @Nonnull
    Set<Modifier> getModifiers();

    @Nonnull
    List<Annotation> getAnnotations();
  }

  public static interface Host extends Node {
  }

  public static abstract class ClassMember extends FreezableSupport implements Annotated {
    private Set<Modifier> modifiers = new LinkedHashSet<>();
    private List<Annotation> annotations = new ArrayList<>();
    private Comment comment;
    private TypeExpr typeExpr;
    private String name;
    private Host parent;

    @Nonnull
    public final String getName() {
      if (name == null) {
        throw new NullPointerException("Name is null");
      }
      return name;
    }

    public final void setName(@Nonnull String name) {
      checkNonFrozen();
      this.name = name;
    }

    @Override
    @Nonnull
    public final Set<Modifier> getModifiers() {
      return ImmutableSet.copyOf(modifiers);
    }

    public final void addModifier(@Nonnull Modifier modifier) {
      modifiers.add(modifier);
    }

    @Override
    @Nonnull
    public List<Annotation> getAnnotations() {
      return ImmutableList.copyOf(annotations);
    }

    public void addAnnotation(@Nonnull Annotation annotation) {
      this.annotations.add(annotation);
    }

    @Nullable
    public Comment getComment() {
      return comment;
    }

    public void setComment(@Nullable Comment comment) {
      checkNonFrozen();
      this.comment = comment;
    }

    @Nonnull
    public TypeExpr getTypeExpr() {
      if (typeExpr == null) {
        throw new RuntimeException("Type expr has not been assigned yet");
      }

      return typeExpr;
    }

    public void setTypeExpr(@Nonnull TypeExpr typeExpr) {
      checkNonFrozen();
      this.typeExpr = typeExpr;
    }

    @Nonnull
    public Host getParent() {
      if (parent == null) {
        throw new RuntimeException("Parent has not been assigned yet");
      }
      return parent;
    }

    public void setParent(@Nonnull Host parent) {
      checkNonFrozen();
      this.parent = parent;
    }

    @Override
    public final void freeze() {
      onFreezeBegin();
      super.freeze();
      modifiers = ImmutableSet.copyOf(modifiers);
      annotations = ImmutableList.copyOf(annotations);
      onFreeze();
    }

    protected void onFreezeBegin() {
      cannotBeFrozenIf(typeExpr == null, "Type expr has not been assigned yet");
      cannotBeFrozenIf(name == null, "Name is not specified");
      cannotBeFrozenIf(parent == null, "Name is not specified");
    }

    protected void onFreeze() {
      // do nothing
    }
  }

  public static final class ClassDecl extends ClassMember implements Host {
  }

  public static final class Var extends ClassMember {
  }

  public static final class Method extends ClassMember implements Host {
  }


  public static final class ClassModule extends FreezableSupport implements Host {
    private Comment moduleComment;
    private FqName packageName;
    private List<Import> imports = new ArrayList<>();
    private List<ClassDecl> classDecls = new ArrayList<>();

    @Nullable
    public Comment getModuleComment() {
      return moduleComment;
    }

    public void setModuleComment(@Nullable Comment moduleComment) {
      checkNonFrozen();
      this.moduleComment = moduleComment;
    }

    @Nullable
    public FqName getPackageName() {
      return packageName;
    }

    public void setPackageName(@Nullable FqName packageName) {
      checkNonFrozen();
      this.packageName = packageName;
    }

    @Nonnull
    public List<Import> getImports() {
      return ImmutableList.copyOf(imports);
    }

    public void addImport(@Nonnull FqName importName) {
      addImport(importName, false);
    }

    public void addImport(@Nonnull FqName importName, boolean isStatic) {
      checkNonFrozen();
      imports.add(new Import(importName, isStatic));
    }

    @Nonnull
    public List<ClassDecl> getClassDecls() {
      return ImmutableList.copyOf(classDecls);
    }

    public void addClassDecl(@Nonnull ClassDecl classDecl) {
      this.classDecls.add(classDecl);
    }

    @Override
    public void freeze() {
      super.freeze();
      imports = ImmutableList.copyOf(imports);
      classDecls = ImmutableList.copyOf(classDecls);
    }
  }

}
