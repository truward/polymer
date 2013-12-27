package com.truward.polymer.core.generator.model;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.freezable.CannotBeFrozenException;
import com.truward.polymer.core.naming.FqName;
import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.core.freezable.FreezableSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class holder, that contains java code objects
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

  public interface TypeExpr extends Node {
  }

  public interface ClassRef extends TypeExpr {
  }

  public interface Comment extends Expr {
  }


  public static final class Import {
    @Nonnull
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

  public static class Annotated<TSelf> extends FreezableSupport implements Node {
    private List<Modifier> modifiers = new ArrayList<>();
    private List<Annotation> annotations = new ArrayList<>();
    private String name;

    @Nonnull
    public final List<Modifier> getModifiers() {
      return ImmutableList.copyOf(modifiers);
    }

    @SuppressWarnings("unchecked")
    public final TSelf addModifier(@Nonnull Modifier modifier) {
      modifiers.add(modifier);
      return (TSelf) this;
    }

    @Nonnull
    public String getName() {
      if (name == null) {
        throw new NullPointerException("Name is null");
      }
      return name;
    }

    @SuppressWarnings("unchecked")
    public TSelf setName(@Nonnull String name) {
      checkNonFrozen();
      this.name = name;
      return (TSelf) this;
    }

    @Override
    public final void freeze() {
      onFreezeBegin();
      cannotBeFrozenIf(name == null, "Name can not be null");
      super.freeze();
      modifiers = ImmutableList.copyOf(modifiers);
      annotations = ImmutableList.copyOf(annotations);
      onFreeze();
    }

    protected void onFreezeBegin() {
      // do nothing
    }

    protected void onFreeze() {
      // do nothing
    }
  }

  public static final class ClassDecl extends Annotated<ClassDecl> {

  }


  public static final class ClassModule extends FreezableSupport implements Node {
    private Comment moduleComment;
    private FqName packageName;
    private List<Import> imports = new ArrayList<>();

    @Nullable
    public Comment getModuleComment() {
      return moduleComment;
    }

    @Nonnull
    public ClassModule setModuleComment(@Nullable Comment moduleComment) {
      checkNonFrozen();
      this.moduleComment = moduleComment;
      return this;
    }

    @Nullable
    public FqName getPackageName() {
      return packageName;
    }

    @Nonnull
    public ClassModule setPackageName(@Nullable FqName packageName) {
      checkNonFrozen();
      this.packageName = packageName;
      return this;
    }

    @Nonnull
    public List<Import> getImports() {
      return imports;
    }

    @Nonnull
    public ClassModule addImport(@Nonnull FqName importName) {
      return addImport(importName, false);
    }

    @Nonnull
    public ClassModule addImport(@Nonnull FqName importName, boolean isStatic) {
      checkNonFrozen();
      imports.add(new Import(importName, isStatic));
      return this;
    }

    @Override
    public void freeze() {
      super.freeze();
      imports = ImmutableList.copyOf(imports);
    }
  }

}
