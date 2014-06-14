package com.truward.polymer.code;

import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Java semantics tree
 *
 * @author Alexander Shabanov
 */
public interface Jst {

  /** Base class for all the semantics tree nodes */
  interface Node {
    <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E;
  }

  /**
   * Represents different types of expressions
   * @see "JLS 3, chapter 15"
   */
  interface Expression extends Node {
  }

  /**
   * Represents different types of statements
   * @see "JLS 3, chapter 14"
   */
  interface Statement extends Node {
  }

  /**
   * Represents an expression, that identifies itself as a statement in the current context, like
   * method invocation which can be used as an expression and as a statement.
   *
   * @see "JLS 3, section 14.8"
   */
  interface ExpressionStatement extends Statement {
    @Nonnull Expression getExpression();
  }

  /** Represents empty statement (equivalent of semicolon) */
  interface EmptyStatement extends Statement {
  }

  /** Represents empty expression (null-safe pattern) */
  interface EmptyExpression extends Expression {
  }

  /**
   * Represents compilation units (source files) and package declarations (package-info.java).
   * @see "JLS 3, sections 7.3, and 7.4"
   */
  interface Unit extends Node {
    @Nonnull FqName getPackageName();

    @Nonnull List<Import> getImports();

    void setImports(@Nonnull Collection<Import> imports);

    @Nonnull List<Annotation> getAnnotations();

    void setAnnotations(@Nonnull Collection<Annotation> annotations);

    @Nonnull List<Node> getDefinitions();

    void setDefinitions(@Nonnull Collection<? extends Node> definitions);
  }

  /**
   * Represents import statement.
   * @see "JLS 3, section 7.5"
   */
  interface Import extends Node {
    boolean isStatic();

    @Nonnull FqName getImportName();
  }


  //
  // types
  //

  /**
   * Base interface for all the type expressions
   */
  interface TypeExpression extends Expression {}

  interface Annotation extends Node {
    @Nonnull TypeExpression getTypeExpression();

    @Nonnull List<Expression> getArguments();

    void setArguments(@Nonnull Collection<? extends Expression> expressions);
  }

  /**
   * Represents type parameter in generic type expression, e.g. 'T extends Serializable'
   * @see "JLS 3, section 4.4"
   */
  interface TypeParameter extends TypeExpression {
    @Nonnull String getName();

    @Nonnull List<TypeExpression> getTypeBounds();
  }

  /**
   * Represents wildcard type, e.g. '?', '? extends T', '? super U'
   * @see "JLS 3, section 4.5.1"
   */
  interface Wildcard extends TypeExpression {
    @Nullable TypeBoundExpression getBoundExpression();
  }

  /**
   * Represents a part of type parameter, e.g. 'extends Serializable' or 'super T'
   */
  interface TypeBoundExpression extends TypeExpression {
    @Nonnull TypeBoundKind getKind();

    @Nonnull Expression getExpression();
  }

  interface Array extends TypeExpression {
    // TODO: body
  }

  //
  // expressions
  //

  /**
   * Represents identifier, i.e. variable.
   * @see "JLS 3, section 6.5.6.1"
   */
  interface Identifier extends Expression {
    @Nonnull String getName();
  }

  /**
   * Represent selectors, aka Field Access expression.
   * @see "JLS 3, section 15.11"
   */
  interface Selector extends Expression {
    @Nonnull String getName();

    @Nonnull Expression getExpression();
  }

  /**
   * Represents unary expressions.
   * @see "JLS 3, section 15.14 and 15.15"
   */
  interface Unary extends Expression {
    @Nonnull Operator getOperator();

    @Nonnull Expression getExpression();
  }

  /**
   * Represents binary expressions.
   * @see "JLS 3, section 15.17 to 15.24"
   */
  interface Binary extends Expression {
    @Nonnull Operator getOperator();

    @Nonnull Expression getLeftExpression();

    @Nonnull Expression getRightExpression();
  }

  /**
   * Represents method invocation.
   * @see "JLS 3, section 15.2"
   */
  interface Call extends Expression {
    @Nonnull String getMethodName();

    @Nullable Expression getBase();

    @Nonnull List<TypeParameter> getTypeParameters();

    @Nonnull List<Expression> getArguments();
  }

  /**
   * Literal expression - i.e. value.
   * @see "JLS 3, section 15.28"
   */
  interface Literal extends Expression {
    @Nullable Object getValue();
  }

  /**
   * Inline 'if' or conditional operator '(condition) ? (thenPart) : (elsePart)'.
   * @see "JLS 3, section 15.25"
   */
  interface Conditional extends Expression {
    @Nonnull Expression getCondition();

    @Nonnull Expression getThenPart();

    @Nonnull Expression getElsePart();
  }

  /**
   * A parenthesized expression.
   * @see "JLS 3, section 15.8.5"
   */
  interface Parens extends Expression {
    @Nonnull Expression getExpression();
  }

  /**
   * An assignment expression - e.g. assignment with "=".
   * @see "JLS 3, section 15.26.1"
   */
  interface Assignment extends Expression {
    @Nonnull Expression getLeftExpression();

    @Nonnull Expression getRightExpression();
  }

  /**
   * Compound assignment operator - e.g. assignment with "+=", "|=", etc.
   * @see "JLS 3, section 15.26.2"
   */
  interface CompoundAssignment extends Expression {
    @Nonnull Operator getOperator();

    @Nonnull Expression getLeftExpression();

    @Nonnull Expression getRightExpression();
  }

  /**
   * A type cast expression.
   * @see "JLS 3, section 15.16"
   */
  interface TypeCast extends Expression {
    @Nonnull TypeExpression getType();

    @Nonnull Expression getExpression();
  }

  /**
   * An 'instanceof' expression.
   * @see "JLS 3, section 15.20.2"
   */
  interface InstanceOf extends Expression {
    @Nonnull Expression getExpression();

    @Nonnull TypeExpression getType();
  }

  /**
   * An array access expression.
   * @see "JLS 3, section 15.13"
   */
  interface ArrayAccess extends Expression {
    @Nonnull Expression getExpression();

    @Nonnull Expression getIndex();
  }

  /**
   * A new instance of a class - new () expression:
   * enclosing.new Type(Arguments)
   * @see "JLS 3, section 15.9"
   */
  interface NewClass extends Expression {
    @Nullable Expression getEnclosingExpression();

    @Nonnull TypeExpression getType();

    @Nonnull List<TypeParameter> getTypeParameters();

    @Nonnull List<Expression> getArguments();

    @Nullable Block getClassBody();
  }

  /**
   * A new instance of an array - new [...] operation.
   * @see "JLS 3, section 15.10"
   */
  interface NewArray extends Expression {
    @Nonnull TypeExpression getType();

    @Nonnull List<Expression> getDimensions();

    @Nonnull List<Expression> getInitializers();
  }

  //
  // statements
  //

  interface NamedStatement extends Statement {
    @Nonnull String getName();

    @Nonnull List<Annotation> getAnnotations();

    void setAnnotations(@Nonnull Collection<? extends Annotation> annotations);

    @Nonnull Set<Modifier> getModifiers();

    void setModifiers(@Nonnull List<Modifier> modifiers);

    // childs?
  }

  interface Package extends NamedStatement {
    boolean isRoot();
  }

  interface VarDeclaration extends NamedStatement {
    @Nonnull TypeExpression getType();

    @Nullable Expression getInitializer();

    void setInitializer(@Nullable Expression expression);
  }

  /**
   * Represents a class, interface, enum, or annotation type declaration.
   * @see "JLS 3, sections 8.1, 8.9, 9.1, and 9.6"
   */
  interface ClassDeclaration extends NamedStatement {
    @Nullable TypeExpression getSuperclass();

    void setSuperclass(@Nonnull TypeExpression superclass);

    @Nonnull List<TypeExpression> getInterfaces();

    void setInterfaces(@Nonnull Collection<? extends TypeExpression> interfaces);

    @Nonnull List<TypeParameter> getTypeParameters();

    void setTypeParameters(@Nonnull Collection<? extends TypeParameter> typeParameters);

    @Nonnull Block getBody();
  }

  /**
   * Represents a method or annotation type element declaration.
   * @see "JLS 3, sections 8.4, 8.6, 8.7, 9.4, and 9.6"
   */
  interface MethodDeclaration extends NamedStatement {
    @Nonnull TypeExpression getReturnType();

    void setReturnType(@Nonnull TypeExpression returnType);

    @Nullable Expression getDefaultValue();

    void setDefaultValue(@Nullable Expression defaultValue);

    @Nullable Block getBody();

    void setBody(@Nullable Block body);

    @Nullable List<VarDeclaration> getArguments();

    void setArguments(@Nonnull List<VarDeclaration> arguments);

    @Nonnull List<Expression> getThrown();

    void setThrown(@Nonnull List<Expression> thrown);
  }

  /**
   * Represents statement block, i.e. method body
   * @see "JLS 3, section 14.2"
   */
  interface Block extends Statement {
    @Nonnull List<Statement> getStatements();

    void addStatement(@Nonnull Statement statement);

    void setStatements(@Nonnull List<Statement> statements);
  }

  /**
   * Represents class initializer block
   * @see "JLS 3, section 14.2"
   */
  interface InitializerBlock extends Statement {
    boolean isStatic();

    @Nonnull List<Statement> getStatements();

    void addStatement(@Nonnull Statement statement);
  }

  interface If extends Statement {
    @Nonnull Expression getCondition();

    @Nonnull Statement getThenPart();

    @Nullable Statement getElsePart();
  }

  /**
   * A 'do' statement.
   * @see "JLS 3, section 14.13"
   */
  interface DoWhileLoop extends Statement {
    @Nonnull Expression getCondition();

    @Nonnull Statement getBody();
  }

  /**
   * A 'while' loop statement.
   * @see "JLS 3, section 14.12"
   */
  interface WhileLoop extends Statement {
    @Nonnull Expression getCondition();

    @Nonnull Statement getBody();
  }

  /**
   * A for loop statement
   * @see "JLS 3, section 14.14.1"
   */
  interface ForLoop extends Statement {
    @Nonnull List<Statement> getInitializers();

    @Nonnull Expression getCondition();

    @Nonnull Expression getStep();

    @Nonnull Statement getBody();
  }

  /**
   * A foreach loop statement.
   * @see "JLS 3, section 14.14.2"
   */
  interface ForEachLoop extends Statement {
    @Nonnull VarDeclaration getVariable();

    @Nonnull Expression getExpression();

    @Nonnull Statement getBody();
  }

  /**
   * A labeled statement.
   * @see "JLS 3, section 14.7"
   */
  interface Labeled extends Statement {
    @Nonnull String getLabel();

    @Nonnull Statement getBody();
  }

  /**
   * A 'case' in a 'switch' statement.
   * @see "JLS 3, section 14.11"
   */
  interface Case extends Statement {
    @Nonnull Expression getExpression();

    @Nonnull List<Statement> getStatements();
  }

  /**
   * A 'switch' statement.
   * @see "JLS 3, section 14.11"
   */
  interface Switch extends Statement {
    @Nonnull Expression getSelector();

    @Nonnull List<Case> getCases();
  }

  /**
   * A 'synchronized' statement.
   * @see "JLS 3, section 14.19"
   */
  interface Synchronized extends Statement {
    @Nonnull Expression getLock();

    @Nonnull Block getBody();
  }

  /**
   * A 'catch' block in a 'try' statement.
   * @see "JLS 3, section 14.20"
   */
  interface Catch extends Statement {
    @Nonnull VarDeclaration getParameter();

    @Nonnull Block getBody();
  }

  /**
   * A 'try' statement.
   * @see "JLS 3, section 14.20"
   */
  interface Try extends Statement {
    @Nonnull Block getBody();

    @Nonnull List<Catch> getCatchers();

    @Nullable Block getFinalizer();
  }

  /**
   * A 'break' statement.
   * @see "JLS 3, section 14.15"
   */
  interface Break extends Statement {
    @Nullable String getLabel();
  }

  /**
   * A 'continue' statement.
   * @see "JLS 3, section 14.16"
   */
  interface Continue extends Statement {
    @Nullable String getLabel();
  }

  /**
   * A 'return' statement.
   * @see "JLS 3, section 14.17"
   */
  interface Return extends Statement {
    @Nullable Expression getExpression();
  }

  /**
   * A 'throw' statement.
   * @see "JLS 3, section 14.18"
   */
  interface Throw extends Statement {
    @Nonnull Expression getExpression();
  }

  /**
   * A 'assert' statement.
   * @see "JLS 3, section 14.10"
   */
  interface Assert extends Statement {
    @Nonnull Expression getExpression();

    @Nullable Expression getDetail();
  }
}
