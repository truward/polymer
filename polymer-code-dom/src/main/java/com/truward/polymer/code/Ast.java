package com.truward.polymer.code;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Holder for Java Abstract Syntax Tree's nodes.
 *
 * @author Alexander Shabanov
 */
public final class Ast {
  /** Hidden */
  private Ast() {}

  //
  // Abstractions
  //

  /** Base class for all the nodes */
  public static abstract class Node {
  }

  public static abstract class Expr extends Node {}

  public static abstract class Stmt extends Node {}

  public static abstract class TypeExpr extends Node {
    public boolean isNilType() {
      return false;
    }
  }


  //
  // Final classes (implementations)
  //

  public static final class NilType extends TypeExpr {
    public static final NilType INSTANCE = new NilType(); // singleton

    /** Hidden */
    private NilType() {}

    @Override
    public boolean isNilType() {
      return true;
    }
  }

  public static final class Array extends TypeExpr {
    private TypeExpr type = NilType.INSTANCE;

    @Nonnull public TypeExpr getType() {
      return type;
    }

    public void setType(@Nonnull TypeExpr type) {
      this.type = type;
    }
  }

  public static final class ClassRef extends TypeExpr {
    private final Class<?> classRef;

    public ClassRef(@Nonnull Class<?> classRef) {
      if (classRef.isArray() || classRef.isSynthetic()) {
        throw new IllegalArgumentException("Non-array (plain) class expected");
      }

      this.classRef = classRef;
    }

    @Nonnull public Class<?> getClassRef() {
      return classRef;
    }
  }

  public static final class Annotation extends Node {
    private TypeExpr typeExpr = NilType.INSTANCE;

    @Nonnull public TypeExpr getTypeExpr() {
      return typeExpr;
    }

    public void setTypeExpr(@Nonnull TypeExpr typeExpr) {
      this.typeExpr = typeExpr;
    }
  }

  public static final class Modifiers extends Node {
    private final Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
    private final List<Annotation> annotations = new ArrayList<>();

    @Nonnull public Set<Modifier> getModifiers() {
      return modifiers;
    }

    @Nonnull public List<Annotation> getAnnotations() {
      return annotations;
    }
  }

  /**
   * Class, Interface, Enum or Annotation Declaration.
   * @see "JLS 3, sections 8.1, 8.9, 9.1, and 9.6"
   */
  public static final class ClassDecl extends Stmt {
    private final Modifiers modifiers = new Modifiers();
    private final TypeExpr superclass = NilType.INSTANCE;
    private final List<TypeExpr> interfaces = new ArrayList<>();

    @Nonnull public Modifiers getModifiers() {
      return modifiers;
    }

    @Nonnull public TypeExpr getSuperclass() {
      return superclass;
    }

    @Nonnull public List<TypeExpr> getInterfaces() {
      return interfaces;
    }
  }
}
