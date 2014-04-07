package com.truward.polymer.code;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.*;

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
  public interface Node {

    /**
     * Designates nil node, i.e. node that has no meaning rather than to identify that this node is not null
     * but it can not be analyzed. Nil nodes in AST represent a 'null object pattern', so all the non-sequence
     * node fields will be initialized with such sentinels
     *
     * @return True, if this is a nil, 'sentinel' object
     */
    boolean isNil();
  }

  /** Represents certain named node */
  public interface Named extends Node {
    @Nonnull String getName();
  }

  /**
   * Represents different types of expressions
   * @see "JLS 3, chapter 15"
   */
  public interface Expr extends Node {}

  /**
   * Represents different types of statements
   * @see "JLS 3, chapter 14"
   */
  public interface Stmt extends Node {}

  /**
   * Represents a type expression, i.e. any usage of type, except for the class declaration
   */
  public interface TypeExpr extends Expr {}

  //
  // Abstract classes
  //

  /** Base abstract class for all the nodes */
  public static abstract class AbstractNode implements Node {
    @Override public final boolean isNil() {
      return false;
    }
  }

  public static abstract class AbstractStmt extends AbstractNode implements Stmt {}

  public static abstract class AbstractExpr extends AbstractNode implements Expr {}

  public static abstract class AbstractTypeExpr extends AbstractNode implements TypeExpr {}


  //
  // Final classes (implementations)
  //

  /** Special sentinel class, represents a 'null object pattern' */
  public static final class Nil implements Expr, Stmt, TypeExpr {
    private Nil() {} // hidden

    public static final Nil INSTANCE = new Nil(); // singleton

    @Override
    public boolean isNil() {
      return true;
    }

    @Override
    public String toString() {
      return "<nil>";
    }
  }

  /**
   * Represents identifier, e.g. variable name.
   * @see "JLS 3, section 6.5.6.1"
   */
  public static final class Ident extends AbstractExpr {
    private final String name;

    public Ident(@Nonnull String name) {
      this.name = name;
    }

    @Nonnull public String getName() {
      return name;
    }
  }

  /**
   * Represents statement block, i.e. method body
   * NB: static initializer block should be represented as a separate entity
   * @see "JLS 3, section 14.2"
   */
  public static final class Block extends AbstractStmt {
    private final List<Stmt> statements = new ArrayList<>();

    @Nonnull public Block addStatement(@Nonnull Stmt statement) {
      statements.add(statement);
      return this;
    }

    @Nonnull public List<Stmt> getStatements() {
      return statements;
    }
  }

  public static final class Array extends AbstractTypeExpr {
    private TypeExpr type = Nil.INSTANCE;

    @Nonnull public TypeExpr getType() {
      return type;
    }

    public void setType(@Nonnull TypeExpr type) {
      this.type = type;
    }
  }

  public static final class ParameterizedClass extends AbstractTypeExpr {

  }

  public static final class ClassRef extends AbstractTypeExpr {
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

  public static final class Annotation extends AbstractNode {
    private TypeExpr typeExpr = Nil.INSTANCE;

    @Nonnull public TypeExpr getTypeExpr() {
      return typeExpr;
    }

    public void setTypeExpr(@Nonnull TypeExpr typeExpr) {
      this.typeExpr = typeExpr;
    }
  }

  public static final class Modifiers extends AbstractNode {
    private final Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
    private final List<Annotation> annotations = new ArrayList<>();

    @Nonnull public Set<Modifier> getModifiers() {
      return modifiers;
    }

    @Nonnull public List<Annotation> getAnnotations() {
      return annotations;
    }
  }

  public static final class Package extends AbstractNode implements Named {
    private final Package parent; // TODO: nil object?
    private final String name;
    private final Map<String, Named> childs = new HashMap<>();

    public Package(@Nullable Package parent, @Nonnull String name) {
      if (parent != null) {
        parent.addChild(this);
      }
      this.parent = parent;
      this.name = name;
    }

    @Nonnull Package addChild(@Nonnull Named node) {
      final String childName = node.getName();
      if (childs.containsKey(childName)) {
        throw new IllegalStateException("Duplicate entry: " + childName);
      }
      childs.put(childName, node);
      return this;
    }

    @Nonnull public Map<String, Named> getChilds() {
      return childs;
    }

    // TODO: Nil object?
    @Nullable public Package getParent() {
      return parent;
    }

    @Override @Nonnull public String getName() {
      return name;
    }

    @Nonnull public FqName getFqName() {
      return getFqNameFromPackage(this);
    }

    @Nonnull private static FqName getFqNameFromPackage(@Nonnull Package pkg) {
      if (pkg.parent == null) {
        return new FqName(pkg.getName(), null); // root package
      }
      return getFqNameFromPackage(pkg.parent).append(pkg.getName());
    }
  }

  /**
   * Represents type parameter in generic type expression, e.g. 'T extends Serializable'
   * @see "JLS 3, section 4.4"
   */
  public static final class TypeParameter extends AbstractTypeExpr {
    private final String name;
    private List<TypeExpr> typeBounds = new ArrayList<>();

    public TypeParameter(@Nonnull String name) {
      this.name = name;
    }

    @Nonnull public String getName() {
      return name;
    }

    @Nonnull public List<TypeExpr> getTypeBounds() {
      return typeBounds;
    }
  }

  /**
   * Represents wildcard type, e.g. '?', '? extends T', '? super U'
   * @see "JLS 3, section 4.5.1"
   */
  public static final class Wildcard extends AbstractTypeExpr {
    private final TypeBoundExpr boundExpr;

    public Wildcard(@Nonnull TypeBoundExpr boundExpr) {
      this.boundExpr = boundExpr;
    }

    public Wildcard() {
      this(new TypeBoundExpr());
    }

    @Nonnull public TypeBoundExpr getBoundExpr() {
      return boundExpr;
    }
  }

  /**
   * Represents a part of type parameter, e.g. 'extends Serializable' or 'super T'
   */
  public static final class TypeBoundExpr extends AbstractExpr {
    private final TypeBoundKind boundKind;
    private final Expr boundExpr;

    public TypeBoundExpr(TypeBoundKind boundKind, Expr boundExpr) {
      this.boundKind = boundKind;
      this.boundExpr = boundExpr;
    }

    public TypeBoundExpr() {
      this(TypeBoundKind.UNBOUND, Nil.INSTANCE);
    }

    @Nonnull public TypeBoundKind getBoundKind() {
      return boundKind;
    }

    @Nonnull public Expr getBoundExpr() {
      return boundExpr;
    }
  }

  /**
   * Class, Interface, Enum or Annotation Declaration.
   * @see "JLS 3, sections 8.1, 8.9, 9.1, and 9.6"
   */
  public static final class ClassDecl extends AbstractStmt implements SynteticClass, Named {
    private final Modifiers modifiers = new Modifiers();
    private TypeExpr superclass = Nil.INSTANCE;
    private final List<TypeExpr> interfaces = new ArrayList<>();
    private final List<Stmt> bodyStmts = new ArrayList<>();
    private final List<TypeParameter> typeParameters = new ArrayList<>();
    private String name;
    private Node parent;

    @Nonnull public ClassDecl makePublicFinal() {
      final Set<Modifier> mods = this.modifiers.getModifiers();
      mods.add(Modifier.PUBLIC);
      mods.add(Modifier.FINAL);
      return this;
    }

    @Nonnull public ClassDecl setSuperclass(@Nonnull TypeExpr superclass) {
      this.superclass = superclass;
      return this;
    }

    @Nonnull public ClassDecl addInterface(@Nonnull TypeExpr iface) {
      this.interfaces.add(iface);
      return this;
    }

    @Nonnull public ClassDecl addStmt(@Nonnull Stmt stmt) {
      this.bodyStmts.add(stmt);
      return this;
    }

    @Nonnull public Modifiers getModifiers() {
      return modifiers;
    }

    @Nonnull public TypeExpr getSuperclass() {
      return superclass;
    }

    @Nonnull public List<TypeExpr> getInterfaces() {
      return interfaces;
    }

    @Nonnull public List<Stmt> getBodyStmts() {
      return bodyStmts;
    }

    @Nonnull public List<TypeParameter> getTypeParameters() {
      return typeParameters;
    }

    @Override @Nonnull public String getName() {
      if (name == null) {
        throw new IllegalStateException("Name can't be accessed for anonymous class");
      }
      return name;
    }

    public void setName(@Nonnull String name) {
      assert !isAnonymousClass();
      this.name = name;
    }

    public Node getParent() {
      return parent;
    }

    public void setParent(@Nonnull Node parent) {
      this.parent = parent;
    }

    @Nonnull
    @Override
    public FqName getFqName() {
      if (isAnonymousClass()) {
        throw new IllegalStateException("Anonymous class has no fully qualified name");
      }

      final NameVisitor nameVisitor = new NameVisitor();
      AstVoidVisitor.apply(this, nameVisitor);
      nameVisitor.appendName(FqName.parse(getName()));
      return nameVisitor.fqName;
    }

    public boolean isAnonymousClass() {
      return name == null;
    }

    private static final class NameVisitor extends AstVoidVisitor {
      public FqName fqName;

      @Override
      public void visitPackage(@Nonnull Package node) {
        appendName(node.getFqName());
      }

      @Override
      public void visitClassDecl(@Nonnull ClassDecl node) {
        appendName(node.getFqName());
      }

      private void appendName(@Nonnull FqName name) {
        if (fqName == null) {
          fqName = name;
        } else {
          fqName = fqName.append(name);
        }
      }
    }
  }

  /**
   * Literal expression - i.e. value.
   * @see "JLS 3, section 15.28"
   */
  public static final class Literal extends AbstractExpr {
    private final Object value;

    public Literal(@Nullable Object value) {
      this.value = value;
    }

    @Nullable public Object getValue() {
      return value;
    }
  }

  /**
   * Return statement.
   * @see "JLS 3, section 14.17"
   */
  public static final class Return extends AbstractStmt {
    private Expr expr = Nil.INSTANCE;

    @Nonnull public Expr getExpr() {
      return expr;
    }

    public void setExpr(@Nonnull Expr expr) {
      this.expr = expr;
    }
  }

  /**
   * Represents a method declaration.
   * @see "JLS 3, sections 8.4, 8.6, 8.7, 9.4, and 9.6"
   */
  public static final class MethodDecl extends AbstractStmt implements Named {
    private final Modifiers modifiers = new Modifiers();
    private final String name;
    private final List<TypeParameter> typeParameters = new ArrayList<>();
    private final List<Expr> thrown = new ArrayList<>();
    private TypeExpr returnType = Nil.INSTANCE;
    private Expr defaultValue = Nil.INSTANCE; // for annotation types
    private Block body; // null for interface/abstract methods

    public MethodDecl(String name) {
      this.name = name;
    }

    @Nonnull public Modifiers getModifiers() {
      return modifiers;
    }

    @Override @Nonnull public String getName() {
      return name;
    }

    @Nonnull public List<TypeParameter> getTypeParameters() {
      return typeParameters;
    }

    @Nonnull public List<Expr> getThrown() {
      return thrown;
    }

    @Nonnull public TypeExpr getReturnType() {
      return returnType;
    }

    @Nonnull public Expr getDefaultValue() {
      return defaultValue;
    }

    @Nullable public Block getBody() {
      return body;
    }
  }
}
