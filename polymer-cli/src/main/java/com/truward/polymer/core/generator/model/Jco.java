package com.truward.polymer.core.generator.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.core.naming.FqName;
import com.truward.polymer.core.freezable.Freezable;
import com.truward.polymer.core.freezable.FreezableSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    @Nonnull
    List<Expr> getExprs();

    void addExpr(@Nonnull Expr expr);
  }

  public static final class SimpleComment implements Comment {
    private List<Expr> exprs = new ArrayList<>();

    @Nonnull
    @Override
    public List<Expr> getExprs() {
      return ImmutableList.copyOf(exprs);
    }

    @Override
    public void addExpr(@Nonnull Expr expr) {
      exprs.add(expr);
    }

    @Override
    public void freeze() {
      exprs = ImmutableList.copyOf(exprs);
    }
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

  public interface ClassMember extends Annotated {
    @Nonnull
    String getName();
  }

  public static class AnnotatedSupport extends FreezableSupport implements Annotated {
    private Set<Modifier> modifiers = new LinkedHashSet<>();
    private List<Annotation> annotations = new ArrayList<>();
    private Comment comment;

    @Override
    @Nonnull
    public final Set<Modifier> getModifiers() {
      return ImmutableSet.copyOf(modifiers);
    }

    @SuppressWarnings("unchecked")
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

    @Override
    public final void freeze() {
      onFreezeBegin();
      super.freeze();
      modifiers = ImmutableSet.copyOf(modifiers);
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

  public static final class ClassDecl extends AnnotatedSupport implements ClassMember {
    private FqName fqName;

    @Nonnull
    @Override
    public String getName() {
      return getFqName().getName();
    }

    @Nonnull
    public FqName getFqName() {
      if (fqName == null) {
        throw new NullPointerException("Name is null");
      }
      return fqName;
    }

    @SuppressWarnings("unchecked")
    public void setFqName(@Nonnull FqName name) {
      checkNonFrozen();
      this.fqName = name;
    }

    @Override
    protected void onFreezeBegin() {
      cannotBeFrozenIf(fqName == null, "Fully qualified name is not specified");
    }
  }

  public static abstract class SimpleClassMember<TSelf> extends AnnotatedSupport implements ClassMember {
    private String name;
    private TypeExpr typeExpr;

    @Nonnull
    public String getName() {
      if (name == null) {
        throw new NullPointerException("Name is null");
      }
      return name;
    }

    @SuppressWarnings("unchecked")
    public void setName(@Nonnull String name) {
      checkNonFrozen();
      this.name = name;
    }

    @Override
    protected void onFreezeBegin() {
      cannotBeFrozenIf(name == null, "Name is not specified");
      cannotBeFrozenIf(typeExpr == null, "Type is not specified");
    }
  }


  public static final class ClassModule extends FreezableSupport implements Node {
    private Comment moduleComment;
    private FqName packageName;
    private List<Import> imports = new ArrayList<>();
    private List<ClassDecl> classDecls = new ArrayList<>();

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
