package com.truward.polymer.code;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.code.visitor.AstVoidVisitor;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.*;

/**
 * Holder for Java Abstract Syntax Tree's nodes.
 *
 * TODO: clear separations to interfaces and implementations
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

    //
    // Methods for named elements
    //

    boolean hasName();

    @Nonnull String getName();

    <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T;
  }

  /**
   * Represents different types of expressions
   * @see "JLS 3, chapter 15"
   */
  public interface Expr extends Node {}

  /**
   * Represents a compilation unit, which corresponds to the particular class
   */
  public interface Unit extends Node {
    @Nonnull List<Import> getImports();
    void setImports(@Nonnull List<Import> imports);
  }

  /**
   * Represents different types of statements
   * @see "JLS 3, chapter 14"
   */
  public interface Stmt extends Node {}

  public interface StmtBlock extends Stmt {
    void addStatement(@Nonnull Stmt statement);

    @Nonnull List<Stmt> getStatements();
  }

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

    @Nonnull @Override public String getName() {
      throw new UnsupportedOperationException();
    }

    @Override public boolean hasName() {
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
  public static final class Nil implements Expr, TypeExpr, StmtBlock, Unit {
    private Nil() {} // hidden

    public static final Nil INSTANCE = new Nil(); // singleton

    @Override public boolean isNil() {
      return true;
    }

    @Nonnull @Override public String getName() {
      throw new UnsupportedOperationException();
    }

    @Override public boolean hasName() {
      return false;
    }

    @Override public void addStatement(@Nonnull Stmt statement) {
      throw new UnsupportedOperationException();
    }

    @Nonnull @Override public List<Stmt> getStatements() {
      return ImmutableList.of();
    }

    @Nonnull @Override public List<Import> getImports() {
      throw new UnsupportedOperationException();
    }

    @Override public void setImports(@Nonnull List<Import> imports) {
      throw new UnsupportedOperationException();
    }

    @Override public String toString() {
      return "<nil>";
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitNil(this);
    }
  }

  /**
   * Represents identifier, e.g. variable importName.
   * @see "JLS 3, section 6.5.6.1"
   */
  public static final class Ident extends AbstractExpr {
    private final String name;

    Ident(@Nonnull String name) {
      this.name = name;
    }

    @Override @Nonnull public String getName() {
      return name;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitIdent(this);
    }
  }

  /**
   * Represent selectors, aka Field Access expression.
   * @see "JLS 3, section 15.11"
   */
  public static final class Select extends AbstractExpr {
    private final Expr expr;
    private final String name;

    Select(@Nonnull Expr expr, @Nonnull String name) {
      this.name = name;
      this.expr = expr;
    }

    @Override @Nonnull public String getName() {
      return name;
    }

    @Nonnull public Expr getExpr() {
      return expr;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitSelect(this);
    }
  }

  public static final class Import extends AbstractNode {
    private final boolean isStatic;
    private final FqName importName;

    Import(boolean isStatic, @Nonnull FqName importName) {
      this.isStatic = isStatic;
      this.importName = importName;
    }

    public boolean isStatic() {
      return isStatic;
    }

    @Nonnull public FqName getImportName() {
      return importName;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitImport(this);
    }
  }

  /**
   * Represents statement block, i.e. method body
   * NB: static initializer block should be represented as a separate entity
   * @see "JLS 3, section 14.2"
   */
  public static final class Block extends AbstractStmt implements StmtBlock {
    private final List<Stmt> statements = new ArrayList<>();

    @Override public void addStatement(@Nonnull Stmt statement) {
      statements.add(statement);
    }

    @Nonnull @Override public List<Stmt> getStatements() {
      return statements;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitBlock(this);
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

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitArray(this);
    }
  }

  public static final class ParameterizedClass extends AbstractTypeExpr {
    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitParameterizedClass(this);
    }
  }

  public static final class ClassRef extends AbstractTypeExpr {
    private final Class<?> classRef;
    private transient FqName fqName = null;

    ClassRef(@Nonnull Class<?> classRef) {
      if (classRef.isArray() || classRef.isSynthetic() || classRef.isAnonymousClass()) {
        throw new IllegalArgumentException("Array, syntetic and anonymous classes are forbidden for reference, " +
            "got " + classRef);
      }

      this.classRef = classRef;
    }

    @Nonnull @Override public String getName() {
      return getFqName().getName();
    }

    @Nonnull public FqName getFqName() {
      if (fqName == null) {
        if (classRef.isPrimitive()) {
          fqName = new FqName(classRef.getName(), null);
        } else {
          assert !classRef.isArray(); // should never trigger
          fqName = FqName.valueOf(classRef.getName());
        }
      }
      return fqName;
    }

    @Nonnull public Class<?> getClassRef() {
      return classRef;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitClassRef(this);
    }
  }

  public static final class Annotation extends AbstractNode {
    private TypeExpr typeExpr = Nil.INSTANCE;
    private List<Expr> arguments = new ArrayList<>();

    @Nonnull public TypeExpr getTypeExpr() {
      return typeExpr;
    }

    @Nonnull public Annotation setTypeExpr(@Nonnull TypeExpr typeExpr) {
      this.typeExpr = typeExpr;
      return this;
    }

    @Nonnull public List<Expr> getArguments() {
      return arguments;
    }

    @Nonnull public Annotation addArgument(@Nonnull Expr argument) {
      this.arguments.add(argument);
      return this;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitAnnotation(this);
    }
  }

  /**
   * Variable, Class Declaration or Method
   */
  public static abstract class NamedStmt<TSelf extends NamedStmt> extends AbstractStmt {
    private final Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
    private final List<Annotation> annotations = new ArrayList<>();
    private String name;
    private Node parent = Nil.INSTANCE;

    protected abstract @Nonnull TSelf getSelf();

    public final TSelf addModifiers(Collection<Modifier> modifiers) {
      this.modifiers.addAll(modifiers);
      return getSelf();
    }

    public final TSelf addModifiers(Modifier... modifiers) {
      return addModifiers(Arrays.asList(modifiers));
    }

    @Nonnull public final Set<Modifier> getModifiers() {
      return modifiers;
    }

    @Nonnull public final List<Annotation> getAnnotations() {
      return annotations;
    }

    @Nonnull public final TSelf addAnnotation(@Nonnull Annotation annotation) {
      this.annotations.add(annotation);
      return getSelf();
    }

    @Nonnull @Override public final String getName() {
      if (name == null) {
        throw new IllegalStateException("Name is null");
      }
      return name;
    }

    @Override public final boolean hasName() {
      return name != null;
    }

    public final TSelf setName(@Nonnull String name) {
      this.name = name;
      return getSelf();
    }

    @Nonnull public final Node getParent() {
      return parent;
    }

    public TSelf setParent(@Nonnull Node parent) {
      assert parent != this : "This class should not be a parent of itself";
      this.parent = parent;
      return getSelf();
    }
  }

  public static final class Package extends NamedStmt<Package> {
    private final Map<String, Node> childs = new HashMap<>();

    Package(@Nonnull Node parent, @Nonnull String name) {
      if (!parent.isNil()) {
        // TODO: visitor
        ((Package) parent).addChild(this);
      }
      setParent(parent);
      setName(name);
    }

    @Nonnull Package addChild(@Nonnull Node node) {
      if (!node.hasName()) {
        throw new UnsupportedOperationException("Only named nodes can be added to the package");
      }

      final String childName = node.getName();
      if (childs.containsKey(childName)) {
        throw new IllegalStateException("Duplicate entry: " + childName);
      }
      childs.put(childName, node);
      return this;
    }

    @Nonnull public Map<String, Node> getChilds() {
      return childs;
    }

    @Nonnull public FqName getFqName() {
      return getFqNameFromPackage(this);
    }

    @Nonnull private static FqName getFqNameFromPackage(@Nonnull Node pkgNode) {
      final Package pkg = (Package) pkgNode; // TODO: visitor
      if (pkg.getParent().isNil()) {
        return new FqName(pkg.getName(), null); // root package
      }
      return getFqNameFromPackage(pkg.getParent()).append(pkg.getName());
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitPackage(this);
    }

    @Nonnull @Override protected Package getSelf() {
      return this;
    }
  }

  /**
   * Represents type parameter in generic type expression, e.g. 'T extends Serializable'
   * @see "JLS 3, section 4.4"
   */
  public static final class TypeParameter extends AbstractTypeExpr {
    private final String name;
    private List<TypeExpr> typeBounds = new ArrayList<>();

    TypeParameter(@Nonnull String name) {
      this.name = name;
    }

    @Nonnull public String getName() {
      return name;
    }

    @Nonnull public List<TypeExpr> getTypeBounds() {
      return typeBounds;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitTypeParameter(this);
    }
  }

  /**
   * Represents wildcard type, e.g. '?', '? extends T', '? super U'
   * @see "JLS 3, section 4.5.1"
   */
  public static final class Wildcard extends AbstractTypeExpr {
    private final TypeBoundExpr boundExpr;

    Wildcard(@Nonnull TypeBoundExpr boundExpr) {
      this.boundExpr = boundExpr;
    }

    Wildcard() {
      this(new TypeBoundExpr());
    }

    @Nonnull public TypeBoundExpr getBoundExpr() {
      return boundExpr;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitWildcard(this);
    }
  }

  /**
   * Represents a part of type parameter, e.g. 'extends Serializable' or 'super T'
   */
  public static final class TypeBoundExpr extends AbstractExpr {
    private final TypeBoundKind boundKind;
    private final Expr boundExpr;

    TypeBoundExpr(TypeBoundKind boundKind, Expr boundExpr) {
      this.boundKind = boundKind;
      this.boundExpr = boundExpr;
    }

    TypeBoundExpr() {
      this(TypeBoundKind.UNBOUND, Nil.INSTANCE);
    }

    @Nonnull public TypeBoundKind getBoundKind() {
      return boundKind;
    }

    @Nonnull public Expr getBoundExpr() {
      return boundExpr;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitTypeBoundExpr(this);
    }
  }

  public static final class CompilationUnit extends AbstractNode implements Unit {
    private List<Import> imports = ImmutableList.of();

    CompilationUnit() {
    }

    @Nonnull @Override public List<Import> getImports() {
      return imports;
    }

    @Override public void setImports(@Nonnull List<Import> imports) {
      this.imports = ImmutableList.copyOf(imports);
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitCompilationUnit(this);
    }
  }

  /**
   * Field, Variable or Method Parameter.
   */
  public static final class VarDecl extends NamedStmt<VarDecl> {
    private TypeExpr typeExpr = Nil.INSTANCE;
    private Expr initializer = Nil.INSTANCE;

    public VarDecl() {
      super();
    }

    public VarDecl(@Nonnull String name) {
      setName(name);
    }

    @Nonnull public TypeExpr getTypeExpr() {
      return typeExpr;
    }

    @Nonnull public VarDecl setTypeExpr(@Nonnull TypeExpr typeExpr) {
      this.typeExpr = typeExpr;
      return this;
    }

    @Nonnull public Expr getInitializer() {
      return initializer;
    }

    @Nonnull public VarDecl setInitializer(@Nonnull Expr initializer) {
      this.initializer = initializer;
      return this;
    }

    @Nonnull @Override protected VarDecl getSelf() {
      return this;
    }

    @Nonnull public Kind getFieldKind() {
      if (getParent().isNil()) {
        return Kind.UNDEFINED;
      }

      if (getParent() instanceof ClassDecl) {
        return Kind.FIELD;
      }

      if (getParent() instanceof MethodDecl) {
        return Kind.PARAMETER;
      }

      if (getParent() instanceof Stmt) {
        return Kind.VAR;
      }

      throw new IllegalStateException("Can't determine field kind based on parent=" + getParent());
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitVarDecl(this);
    }

    public static enum Kind {
      UNDEFINED,
      FIELD,
      VAR,
      PARAMETER
    }
  }

  /**
   * Class, Interface, Enum or Annotation Declaration.
   * @see "JLS 3, sections 8.1, 8.9, 9.1, and 9.6"
   */
  public static final class ClassDecl extends NamedStmt<ClassDecl> implements SynteticClass {
    private TypeExpr superclass = Nil.INSTANCE;
    private final List<TypeExpr> interfaces = new ArrayList<>();
    private final List<Stmt> bodyStmts = new ArrayList<>();
    private final List<TypeParameter> typeParameters = new ArrayList<>();
    private Unit compilationUnit = Nil.INSTANCE;

    ClassDecl() {
    }

    @Nonnull public ClassDecl setCompilationUnit(@Nonnull Unit unit) {
      this.compilationUnit = unit;
      return this;
    }

    @Nonnull public Unit getCompilationUnit() {
      return compilationUnit;
    }

    @Nonnull public MethodDecl addMethodDecl(@Nonnull String name) {
      final MethodDecl methodDecl = new MethodDecl(name);
      methodDecl.setParent(this);
      addStmt(methodDecl);
      return methodDecl;
    }

    @Nonnull public VarDecl addField(@Nonnull String name, @Nonnull TypeExpr typeExpr) {
      final VarDecl varDecl = new VarDecl(name);
      varDecl.setTypeExpr(typeExpr).setParent(this);
      addStmt(varDecl);
      return varDecl;
    }

    @Nonnull public VarDecl addField(@Nonnull String name) {
      return addField(name, Nil.INSTANCE);
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

    @Nonnull
    @Override
    public FqName getFqName() {
      if (!hasName()) {
        throw new IllegalStateException("Anonymous class has no fully qualified name");
      }

      final NameVisitor nameVisitor = new NameVisitor();
      getParent().accept(nameVisitor);
      nameVisitor.appendName(FqName.valueOf(getName()));
      return nameVisitor.fqName;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitClassDecl(this);
    }

    //
    // Private
    //


    @Override @Nonnull protected ClassDecl getSelf() {
      return this;
    }

    private static final class NameVisitor extends AstVoidVisitor<RuntimeException> {
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

    Literal(@Nullable Object value) {
      this.value = value;
    }

    @Nullable public Object getValue() {
      return value;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitLiteral(this);
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

    @Nonnull public Return setExpr(@Nonnull Expr expr) {
      this.expr = expr;
      return this;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitReturn(this);
    }
  }

  /**
   * Represents a method declaration.
   * @see "JLS 3, sections 8.4, 8.6, 8.7, 9.4, and 9.6"
   */
  public static final class MethodDecl extends NamedStmt<MethodDecl> {
    private final List<TypeParameter> typeParameters = new ArrayList<>();
    private final List<VarDecl> arguments = new ArrayList<>();
    private final List<Expr> thrown = new ArrayList<>();
    private TypeExpr returnType = Nil.INSTANCE; // nil for constructors
    private Expr defaultValue = Nil.INSTANCE; // for annotation types
    private StmtBlock body = Nil.INSTANCE; // nil for interface/abstract methods

    MethodDecl(@Nonnull String name) {
      setName(name);
    }

    @Nonnull public List<VarDecl> getArguments() {
      return arguments;
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

    @Nonnull public StmtBlock getBody() {
      return body;
    }

    @Nonnull public MethodDecl addBodyStmt(@Nonnull Stmt stmt) {
      if (body.isNil()) {
        body = new Block();
      }
      body.addStatement(stmt);
      return this;
    }

    @Nonnull public MethodDecl addArgument(@Nonnull VarDecl argument) {
      argument.setParent(this);
      this.arguments.add(argument);
      return this;
    }

    @Nonnull public MethodDecl addArgument(@Nonnull String name, @Nonnull TypeExpr type) {
      return addArgument(new VarDecl(name).setTypeExpr(type));
    }

    @Nonnull public MethodDecl setReturnType(@Nonnull TypeExpr returnType) {
      this.returnType = returnType;
      return this;
    }

    @Nonnull public MethodDecl setDefaultValue(@Nonnull Expr defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    @Nonnull public MethodDecl addTypeParameter(@Nonnull TypeParameter typeParameter) {
      this.typeParameters.add(typeParameter);
      return this;
    }

    @Override public <T extends Exception> void accept(@Nonnull AstVoidVisitor<T> visitor) throws T {
      visitor.visitMethodDecl(this);
    }

    //
    // Private
    //

    @Nonnull @Override protected MethodDecl getSelf() {
      return this;
    }
  }
}
