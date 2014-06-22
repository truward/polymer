package com.truward.polymer.code.factory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.code.*;
import com.truward.polymer.code.visitor.JstVisitor;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Alexander Shabanov
 */
public final class DefaultJstFactory implements JstFactory {
  // private members
  private final Map<Class<?>, Jst.ClassType> classTypeCache = new HashMap<>(500);
  private final Map<FqName, Jst.SimpleClassType> importedTypes = new HashMap<>(500);

  //
  // Implementation of JstFactory
  //

  @Nonnull @Override public Jst.Unit jstUnit(@Nonnull FqName packageName) {
    return new Unit(packageName);
  }

  @Nonnull @Override public Jst.Import jstImport(@Nonnull FqName importName, boolean isStatic) {
    return new Import(importName, isStatic);
  }

  @Nonnull @Override public Jst.Identifier jstIdentifier(@Nonnull String name) {
    return new Identifier(name);
  }

  @Nonnull @Override public Jst.Selector jstSelector(@Nonnull Jst.Expression base, @Nonnull String name) {
    return new Selector(name, base);
  }

  @Nonnull @Override public Jst.Literal jstLiteral(@Nullable Object value) {
    return new Literal(value);
  }

  @Nonnull @Override public Jst.Annotation jstAnnotation(@Nonnull Jst.TypeExpression annotationType) {
    return new Annotation(annotationType);
  }

  @Nonnull @Override public Jst.ClassDeclaration jstClass(@Nonnull String name) {
    return new ClassDeclaration(name, jstBlock());
  }

  @Nonnull @Override public Jst.MethodDeclaration jstMethod(@Nonnull String name) {
    return new MethodDeclaration(name, jstClassType(Void.TYPE));
  }

  @Nonnull @Override public Jst.VarDeclaration jstVar(@Nonnull String name, @Nonnull Jst.TypeExpression type) {
    return new VarDeclaration(name, type);
  }

  @Nonnull @Override public Jst.EmptyStatement jstEmptyStatement() {
    return EmptyStatement.INSTANCE;
  }

  @Nonnull @Override public Jst.EmptyExpression jstEmptyExpression() {
    return EmptyExpression.INSTANCE;
  }

  @Nonnull @Override public Jst.Block jstBlock() {
    return new Block();
  }

  @Nonnull @Override public Jst.InitializerBlock jstInitializerBlock(boolean isStatic) {
    return new InitializerBlock(isStatic);
  }

  @Nonnull @Override public Jst.DoWhileLoop jstDoWhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body) {
    return new DoWhileLoop(condition, body);
  }

  @Nonnull @Override public Jst.WhileLoop jstWhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body) {
    return new WhileLoop(condition, body);
  }

  @Nonnull @Override public Jst.ForLoop jstForLoop(@Nonnull List<Jst.Statement> initializers,
                                                   @Nonnull Jst.Expression condition,
                                                   @Nonnull Jst.Expression step,
                                                   @Nonnull Jst.Statement body) {
    return new ForLoop(initializers, condition, step, body);
  }

  @Nonnull @Override public Jst.ForEachLoop jstForEachLoop(@Nonnull Jst.VarDeclaration var,
                                                           @Nonnull Jst.Expression expression,
                                                           @Nonnull Jst.Statement body) {
    return new ForEachLoop(var, expression, body);
  }

  @Nonnull @Override public Jst.Labeled jstLabeled(@Nonnull String label, @Nonnull Jst.Statement body) {
    return new Labeled(label, body);
  }

  @Nonnull @Override public Jst.Switch jstSwitch(@Nonnull Jst.Expression selector, @Nonnull List<Jst.Case> cases) {
    return new Switch(selector, cases);
  }

  @Nonnull @Override public Jst.Case jstCase(@Nullable Jst.Expression expression) {
    return new Case(expression);
  }

  @Nonnull @Override public Jst.Synchronized jstSynchronized(@Nonnull Jst.Expression lock, @Nonnull Jst.Block body) {
    return new Synchronized(lock, body);
  }

  @Nonnull @Override public Jst.Try jstTry(@Nonnull Jst.Block body,
                                           @Nonnull List<Jst.Catch> catchers,
                                           @Nullable Jst.Block finalizer) {
    return new Try(body, catchers, finalizer);
  }

  @Nonnull @Override public Jst.Catch jstCatch(@Nonnull Jst.VarDeclaration parameter, @Nonnull Jst.Block body) {
    return new Catch(parameter, body);
  }

  @Nonnull @Override public Jst.If jstIf(@Nonnull Jst.Expression condition,
                                         @Nonnull Jst.Statement then,
                                         @Nullable Jst.Statement els) {
    return new If(condition, then, els);
  }

  @Nonnull @Override public Jst.Conditional jstConditional(@Nonnull Jst.Expression condition,
                                                           @Nonnull Jst.Expression thenPart,
                                                           @Nonnull Jst.Expression elsePart) {
    return new Conditional(condition, thenPart, elsePart);
  }

  @Nonnull @Override public Jst.Break jstBreak(@Nullable String label) {
    return new Break(label);
  }

  @Nonnull @Override public Jst.Continue jstContinue(@Nullable String label) {
    return new Continue(label);
  }

  @Nonnull @Override public Jst.Return jstReturn(@Nullable Jst.Expression expression) {
    return new Return(expression);
  }

  @Nonnull @Override public Jst.Throw jstThrow(@Nonnull Jst.Expression expression) {
    return new Throw(expression);
  }

  @Nonnull @Override public Jst.Assert jstAssert(@Nonnull Jst.Expression expression,
                                                 @Nullable Jst.Expression detail) {
    return new Assert(expression, detail);
  }

  @Nonnull @Override public Jst.Call jstCall(@Nonnull Jst.Expression methodName,
                                             @Nonnull Collection<? extends Jst.TypeParameter> typeParameters,
                                             @Nonnull Collection<? extends Jst.Expression> arguments) {
    return new Call(methodName, typeParameters, arguments);
  }

  @Nonnull @Override public Jst.NewClass jstNewClass(@Nullable Jst.Expression enclosingExpression,
                                                     @Nonnull Jst.TypeExpression type,
                                                     @Nonnull Collection<? extends Jst.TypeParameter> typeParameters,
                                                     @Nonnull Collection<? extends Jst.Expression> arguments,
                                                     @Nullable Jst.ClassDeclaration classDeclaration) {
    return new NewClass(enclosingExpression, type, typeParameters, arguments, classDeclaration);
  }

  @Nonnull @Override public Jst.NewArray jstNewArray(@Nonnull Jst.TypeExpression type,
                                                     @Nonnull Collection<? extends Jst.Expression> dimensions,
                                                     @Nonnull Collection<? extends Jst.Expression> initializers) {
    return new NewArray(type, dimensions, initializers);
  }

  @Nonnull @Override public Jst.Parens jstParens(@Nonnull Jst.Expression expression) {
    return new Parens(expression);
  }

  @Nonnull @Override public Jst.Assignment jstAssignment(@Nonnull Jst.Expression left, @Nonnull Jst.Expression right) {
    return new Assignment(left, right);
  }

  @Nonnull @Override public Jst.CompoundAssignment jstCompoundAssignment(@Nonnull Operator operator,
                                                                         @Nonnull Jst.Expression left,
                                                                         @Nonnull Jst.Expression right) {
    return new CompoundAssignment(operator, left, right);
  }

  @Nonnull @Override public Jst.Unary jstUnary(@Nonnull Operator operator, @Nonnull Jst.Expression expression) {
    return new Unary(operator, expression);
  }

  @Nonnull @Override public Jst.Binary jstBinary(@Nonnull Operator operator,
                                                 @Nonnull Jst.Expression left,
                                                 @Nonnull Jst.Expression right) {
    return new Binary(operator, left, right);
  }

  @Nonnull @Override public Jst.ClassType jstClassType(@Nonnull Class<?> clazz) {
    Jst.ClassType result = classTypeCache.get(clazz);
    if (result == null) {
      result = new ClassType(clazz);
      classTypeCache.put(clazz, result);

      final FqName fqName = result.getFqName();
      if (importedTypes.containsKey(fqName)) {
        throw new IllegalStateException("Double import of a class " + fqName);
      }

      importedTypes.put(result.getFqName(), result);
    }

    return result;
  }

  @Nonnull @Override public Jst.SynteticType jstSynteticType(@Nonnull FqName name) {
    final Jst.SimpleClassType result = importedTypes.get(name);
    if (result != null) {
      if (!(result instanceof Jst.SynteticType)) {
        throw new IllegalStateException("Non-syntetic class " + name + " has been already imported ");
      }
      return (Jst.SynteticType) result;
    }

    final SynteticType synteticType = new SynteticType(name);
    importedTypes.put(name, synteticType);
    return synteticType;
  }

  @Nonnull @Override public Jst.Array jstArray(@Nonnull Jst.TypeExpression elementType) {
    return new Array(elementType);
  }

  @Nonnull @Override public Jst.ParameterizedType jstParameterizedType(@Nonnull Jst.TypeExpression type,
                                                                       @Nonnull Collection<? extends Jst.Expression> arguments) {
    return new ParameterizedType(type, arguments);
  }

  @Nonnull @Override public Jst.Wildcard jstWildcard(@Nonnull TypeBoundKind kind, @Nullable Jst.Expression typeBound) {
    return new Wildcard(kind, typeBound);
  }

  @Nonnull @Override public Jst.TypeParameter jstTypeParameter(@Nonnull String name, @Nonnull Collection<? extends Jst.Expression> bounds) {
    return new TypeParameter(name, bounds);
  }

  @Nonnull @Override public Jst.UnionType jstUnionType(@Nonnull Collection<? extends Jst.TypeExpression> types) {
    return new UnionType(types);
  }

  //
  // Private
  //


  private static abstract class AbstractNode implements Jst.Node {

    @Override public String toString() {
      return "Jst." + getClass().getSimpleName() + "#" + hashCode();
    }
  }

  private static final class Annotation extends AbstractNode implements Jst.Annotation {
    private final Jst.TypeExpression typeExpression;
    private List<Jst.Expression> arguments = ImmutableList.of();

    Annotation(@Nonnull Jst.TypeExpression typeExpression) {
      this.typeExpression = typeExpression;
    }

    @Nonnull @Override public Jst.TypeExpression getTypeExpression() {
      return typeExpression;
    }

    @Nonnull @Override public List<Jst.Expression> getArguments() {
      return arguments;
    }

    @Override public void setArguments(@Nonnull Collection<? extends Jst.Expression> expressions) {
      this.arguments = ImmutableList.copyOf(expressions);
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitAnnotation(this);
    }
  }

  private static abstract class AbstractNamed extends AbstractNode implements Jst.Named {
    private final String name;

    AbstractNamed(@Nonnull String name) {
      this.name = name;
    }

    @Nonnull @Override public final String getName() {
      return name;
    }
  }

  private static final class Identifier extends AbstractNamed implements Jst.Identifier {
    Identifier(@Nonnull String name) {
      super(name);
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitIdentifier(this);
    }
  }

  private static final class Selector extends AbstractNamed implements Jst.Selector {
    private final Jst.Expression expression;

    Selector(@Nonnull String name, @Nonnull Jst.Expression expression) {
      super(name);
      this.expression = expression;
    }

    @Nonnull @Override public Jst.Expression getExpression() {
      return expression;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitSelector(this);
    }
  }

  private static final class Literal extends AbstractNode implements Jst.Literal {
    private final Object value;

    Literal(@Nullable Object value) {
      this.value = value;
    }

    @Nullable @Override public Object getValue() {
      return value;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitLiteral(this);
    }
  }

  private static final class Unit extends AbstractNode implements Jst.Unit {

    private final FqName packageName;
    private List<Jst.Import> imports = ImmutableList.of();
    private List<Jst.Annotation> annotations = ImmutableList.of();
    private List<Jst.ClassDeclaration> classDeclarations = ImmutableList.of();

    Unit(FqName packageName) {
      this.packageName = packageName;
    }

    @Nonnull @Override public FqName getPackageName() {
      return packageName;
    }

    @Nonnull @Override public List<Jst.Import> getImports() {
      return imports;
    }

    @Override public void setImports(@Nonnull Collection<Jst.Import> imports) {
      this.imports = ImmutableList.copyOf(imports);
    }

    @Nonnull @Override public List<Jst.Annotation> getAnnotations() {
      return annotations;
    }

    @Override public void setAnnotations(@Nonnull Collection<Jst.Annotation> annotations) {
      this.annotations = ImmutableList.copyOf(annotations);
    }

    @Nonnull @Override public List<Jst.ClassDeclaration> getClasses() {
      return classDeclarations;
    }

    @Override public void setClasses(@Nonnull Collection<Jst.ClassDeclaration> classes) {
      this.classDeclarations = ImmutableList.copyOf(classes);
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitUnit(this);
    }
  }

  private static final class Import extends AbstractNode implements Jst.Import {
    private final FqName importName;
    private final boolean staticImport;

    Import(FqName importName, boolean staticImport) {
      this.importName = importName;
      this.staticImport = staticImport;
    }

    @Override public boolean isStatic() {
      return staticImport;
    }

    @Nonnull @Override public FqName getImportName() {
      return importName;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitImport(this);
    }
  }

  private static abstract class SimpleClassType extends AbstractNode implements Jst.SimpleClassType {
    private final FqName fqName;

    SimpleClassType(@Nonnull FqName fqName) {
      this.fqName = fqName;
    }

    @Nonnull @Override public final FqName getFqName() {
      return fqName;
    }
  }

  private static final class ClassType extends SimpleClassType implements Jst.ClassType {
    private final Class<?> wrappedClass;

    ClassType(@Nonnull Class<?> wrappedClass) {
      super(FqName.valueOf(wrappedClass.getName()));
      if (wrappedClass.isArray() || wrappedClass.isAnonymousClass() || wrappedClass.isSynthetic()) {
        throw new UnsupportedOperationException("Unsupported class: " + wrappedClass +
            " - array, anonymous and syntetic classes are not supported");
      }

      this.wrappedClass = wrappedClass;
    }

    @Nonnull @Override public Class<?> getWrappedClass() {
      return wrappedClass;
    }

    @Override
    public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitClassType(this);
    }
  }

  private static final class SynteticType extends SimpleClassType implements Jst.SynteticType {
    SynteticType(@Nonnull FqName fqName) {
      super(fqName);
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitSynteticType(this);
    }
  }

  private static abstract class StatementList extends AbstractNode implements Jst.StatementList {
    private List<Jst.Statement> statements = ImmutableList.of();

    @Nonnull @Override public final List<Jst.Statement> getStatements() {
      return statements;
    }

    @Override public final void setStatements(@Nonnull Collection<? extends Jst.Statement> statements) {
      this.statements = ImmutableList.copyOf(statements);
    }
  }

  private static final class Block extends StatementList implements Jst.Block {
    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitBlock(this);
    }
  }

  private static final class InitializerBlock extends StatementList implements Jst.InitializerBlock {
    private final boolean staticBlock;

    InitializerBlock(boolean staticBlock) {
      this.staticBlock = staticBlock;
    }

    @Override public boolean isStatic() {
      return staticBlock;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitInitializerBlock(this);
    }
  }

  private static final class DoWhileLoop extends AbstractNode implements Jst.DoWhileLoop {
    private final Jst.Expression condition;
    private final Jst.Statement body;

    DoWhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body) {
      this.condition = condition;
      this.body = body;
    }

    @Nonnull @Override public Jst.Expression getCondition() {
      return condition;
    }

    @Nonnull @Override public Jst.Statement getBody() {
      return body;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitDoWhileLoop(this);
    }
  }

  private static final class WhileLoop extends AbstractNode implements Jst.WhileLoop {
    private final Jst.Expression condition;
    private final Jst.Statement body;

    WhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body) {
      this.condition = condition;
      this.body = body;
    }

    @Nonnull @Override public Jst.Expression getCondition() {
      return condition;
    }

    @Nonnull @Override public Jst.Statement getBody() {
      return body;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitWhileLoop(this);
    }
  }

  private static final class ForLoop extends AbstractNode implements Jst.ForLoop {
    private final List<Jst.Statement> initializers;
    private final Jst.Expression condition;
    private final Jst.Expression step;
    private final Jst.Statement body;

    ForLoop(@Nonnull Collection<? extends Jst.Statement> initializers,
                    @Nonnull Jst.Expression condition,
                    @Nonnull Jst.Expression step,
                    @Nonnull Jst.Statement body) {
      this.initializers = ImmutableList.copyOf(initializers);
      this.condition = condition;
      this.step = step;
      this.body = body;
    }

    @Nonnull @Override public List<Jst.Statement> getInitializers() {
      return initializers;
    }

    @Nonnull @Override public Jst.Expression getCondition() {
      return condition;
    }

    @Nonnull @Override public Jst.Expression getStep() {
      return step;
    }

    @Nonnull @Override public Jst.Statement getBody() {
      return body;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitForLoop(this);
    }
  }

  private static final class ForEachLoop extends AbstractNode implements Jst.ForEachLoop {
    private final Jst.VarDeclaration variable;
    private final Jst.Expression expression;
    private final Jst.Statement body;

    ForEachLoop(@Nonnull Jst.VarDeclaration variable,
                @Nonnull Jst.Expression expression,
                @Nonnull Jst.Statement body) {
      this.variable = variable;
      this.expression = expression;
      this.body = body;
    }

    @Nonnull @Override public Jst.VarDeclaration getVariable() {
      return variable;
    }

    @Nonnull @Override public Jst.Expression getExpression() {
      return expression;
    }

    @Nonnull @Override public Jst.Statement getBody() {
      return body;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitForEachLoop(this);
    }
  }

  private static final class Labeled extends AbstractNode implements Jst.Labeled {
    private final String label;
    private final Jst.Statement body;

    Labeled(@Nonnull String label, @Nonnull Jst.Statement body) {
      this.label = label;
      this.body = body;
    }

    @Nonnull @Override public String getLabel() {
      return label;
    }

    @Nonnull @Override public Jst.Statement getBody() {
      return body;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitLabeled(this);
    }
  }

  private static final class Switch extends AbstractNode implements Jst.Switch {
    private final Jst.Expression selector;
    private final List<Jst.Case> cases;

    Switch(@Nonnull Jst.Expression selector, @Nonnull List<Jst.Case> cases) {
      this.selector = selector;
      this.cases = ImmutableList.copyOf(cases);
    }

    @Nonnull @Override public Jst.Expression getSelector() {
      return selector;
    }

    @Nonnull @Override public List<Jst.Case> getCases() {
      return cases;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitSwitch(this);
    }
  }

  private static final class Case extends StatementList implements Jst.Case {
    private final Jst.Expression expression;

    Case(@Nullable Jst.Expression expression) {
      this.expression = expression;
    }

    @Nullable @Override public Jst.Expression getExpression() {
      return expression;
    }

    @Override
    public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitCase(this);
    }
  }

  private static final class Synchronized extends AbstractNode implements Jst.Synchronized {
    private final Jst.Expression lock;
    private final Jst.Block body;

    Synchronized(@Nonnull Jst.Expression lock, @Nonnull Jst.Block body) {
      this.lock = lock;
      this.body = body;
    }

    @Nonnull @Override public Jst.Expression getLock() {
      return lock;
    }

    @Nonnull @Override public Jst.Block getBody() {
      return body;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitSynchronized(this);
    }
  }

  private static final class Try extends AbstractNode implements Jst.Try {
    private final Jst.Block body;
    private final List<Jst.Catch> catchers;
    private final Jst.Block finalizer;

    Try(@Nonnull Jst.Block body, @Nonnull List<Jst.Catch> catchers, @Nullable Jst.Block finalizer) {
      this.body = body;
      this.catchers = ImmutableList.copyOf(catchers);
      this.finalizer = finalizer;
    }

    @Nonnull @Override public Jst.Block getBody() {
      return body;
    }

    @Nonnull @Override public List<Jst.Catch> getCatchers() {
      return catchers;
    }

    @Nullable @Override public Jst.Block getFinalizer() {
      return finalizer;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitTry(this);
    }
  }

  private static final class Catch extends AbstractNode implements Jst.Catch {
    private final Jst.VarDeclaration parameter;
    private final Jst.Block body;

    Catch(@Nonnull Jst.VarDeclaration parameter, @Nonnull Jst.Block body) {
      this.parameter = parameter;
      this.body = body;
    }

    @Nonnull @Override public Jst.VarDeclaration getParameter() {
      return parameter;
    }

    @Nonnull @Override public Jst.Block getBody() {
      return body;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitCatch(this);
    }
  }

  private static abstract class NamedStatement extends AbstractNamed implements Jst.NamedStatement {
    private List<Jst.Annotation> annotations = ImmutableList.of();
    private Set<JstFlag> flags = ImmutableSet.of();

    NamedStatement(@Nonnull String name) {
      super(name);
    }

    @Nonnull @Override public final List<Jst.Annotation> getAnnotations() {
      return annotations;
    }

    @Override public final void setAnnotations(@Nonnull Collection<Jst.Annotation> annotations) {
      this.annotations = ImmutableList.copyOf(annotations);
    }

    @Nonnull @Override public final Set<JstFlag> getFlags() {
      return flags;
    }

    @Override public final void setFlags(@Nonnull Collection<JstFlag> flags) {
      this.flags = flags.isEmpty() ? EnumSet.noneOf(JstFlag.class) : EnumSet.copyOf(flags);
    }
  }

  private static final class VarDeclaration extends NamedStatement implements Jst.VarDeclaration {
    private final Jst.TypeExpression type;
    private Jst.Expression initializer;

    VarDeclaration(@Nonnull String name, @Nonnull Jst.TypeExpression type) {
      super(name);
      this.type = type;
    }

    @Nonnull @Override public Jst.TypeExpression getType() {
      return type;
    }

    @Nullable @Override public Jst.Expression getInitializer() {
      return initializer;
    }

    @Override public void setInitializer(@Nullable Jst.Expression expression) {
      this.initializer = expression;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitVar(this);
    }
  }

  private static final class ClassDeclaration extends NamedStatement implements Jst.ClassDeclaration {
    private Jst.TypeExpression superclass;
    private List<Jst.TypeExpression> interfaces = ImmutableList.of();
    private List<Jst.TypeParameter> typeParameters = ImmutableList.of();
    private final Jst.Block body;

    ClassDeclaration(@Nonnull String name, @Nonnull Jst.Block body) {
      super(name);
      this.body = body;
    }

    @Nullable @Override public Jst.TypeExpression getSuperclass() {
      return superclass;
    }

    @Override public void setSuperclass(@Nullable Jst.TypeExpression superclass) {
      this.superclass = superclass;
    }

    @Nonnull @Override public List<Jst.TypeExpression> getInterfaces() {
      return interfaces;
    }

    @Override public void setInterfaces(@Nonnull Collection<? extends Jst.TypeExpression> interfaces) {
      this.interfaces = ImmutableList.copyOf(interfaces);
    }

    @Nonnull @Override public List<Jst.TypeParameter> getTypeParameters() {
      return typeParameters;
    }

    @Override public void setTypeParameters(@Nonnull Collection<? extends Jst.TypeParameter> typeParameters) {
      this.typeParameters = ImmutableList.copyOf(typeParameters);
    }

    @Nonnull @Override public Jst.Block getBody() {
      return body;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitClass(this);
    }
  }

  private static final class MethodDeclaration extends NamedStatement implements Jst.MethodDeclaration {
    private Jst.TypeExpression returnType;
    private Jst.Expression defaultValue;
    private Jst.Block body;
    private List<Jst.VarDeclaration> arguments = ImmutableList.of();
    private List<Jst.Expression> thrown = ImmutableList.of();
    private List<Jst.TypeParameter> typeParameters = ImmutableList.of();

    MethodDeclaration(@Nonnull String name, @Nonnull Jst.TypeExpression defaultReturnType) {
      super(name);
      this.returnType = defaultReturnType;
    }

    @Nonnull @Override public List<Jst.TypeParameter> getTypeParameters() {
      return typeParameters;
    }

    @Override public void setTypeParameters(@Nonnull Collection<? extends Jst.TypeParameter> typeParameters) {
      this.typeParameters = ImmutableList.copyOf(typeParameters);
    }

    @Nonnull @Override public Jst.TypeExpression getReturnType() {
      return returnType;
    }

    @Override public void setReturnType(@Nonnull Jst.TypeExpression returnType) {
      this.returnType = returnType;
    }

    @Nullable @Override public Jst.Expression getDefaultValue() {
      return defaultValue;
    }

    @Override public void setDefaultValue(@Nullable Jst.Expression defaultValue) {
      this.defaultValue = defaultValue;
    }

    @Nullable @Override public Jst.Block getBody() {
      return body;
    }

    @Override public void setBody(@Nullable Jst.Block body) {
      this.body = body;
    }

    @Nonnull @Override public List<Jst.VarDeclaration> getArguments() {
      return arguments;
    }

    @Override public void setArguments(@Nonnull Collection<Jst.VarDeclaration> arguments) {
      this.arguments = ImmutableList.copyOf(arguments);
    }

    @Nonnull @Override public List<Jst.Expression> getThrown() {
      return thrown;
    }

    @Override public void setThrown(@Nonnull List<Jst.Expression> thrown) {
      this.thrown = thrown;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitMethod(this);
    }
  }

  private static final class If extends AbstractNode implements Jst.If {
    private final Jst.Expression condition;
    private final Jst.Statement thenPart;
    private final Jst.Statement elsePart;

    If(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement thenPart, @Nullable Jst.Statement elsePart) {
      this.condition = condition;
      this.thenPart = thenPart;
      this.elsePart = elsePart;
    }

    @Nonnull @Override public Jst.Expression getCondition() {
      return condition;
    }

    @Nonnull @Override public Jst.Statement getThenPart() {
      return thenPart;
    }

    @Nullable @Override public Jst.Statement getElsePart() {
      return elsePart;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitIf(this);
    }
  }

  private static final class Conditional extends AbstractNode implements Jst.Conditional {
    private final Jst.Expression condition;
    private final Jst.Expression thenPart;
    private final Jst.Expression elsePart;

    Conditional(@Nonnull Jst.Expression condition, @Nonnull Jst.Expression thenPart, @Nonnull Jst.Expression elsePart) {
      this.condition = condition;
      this.thenPart = thenPart;
      this.elsePart = elsePart;
    }

    @Nonnull @Override public Jst.Expression getCondition() {
      return condition;
    }

    @Nonnull @Override public Jst.Expression getThenPart() {
      return thenPart;
    }

    @Nonnull @Override public Jst.Expression getElsePart() {
      return elsePart;
    }

    @Override
    public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitConditional(this);
    }
  }

  private static final class Break extends AbstractNode implements Jst.Break {
    private final String label;

    Break(@Nullable String label) {
      this.label = label;
    }

    @Nullable @Override public String getLabel() {
      return label;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitBreak(this);
    }
  }

  private static final class Continue extends AbstractNode implements Jst.Continue {
    private final String label;

    Continue(@Nullable String label) {
      this.label = label;
    }

    @Nullable @Override public String getLabel() {
      return label;
    }

    @Override
    public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitContinue(this);
    }
  }

  private static final class Return extends AbstractNode implements Jst.Return {
    private final Jst.Expression expression;

    Return(@Nullable Jst.Expression expression) {
      this.expression = expression;
    }

    @Nullable @Override public Jst.Expression getExpression() {
      return expression;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitReturn(this);
    }
  }

  private static final class Parens extends AbstractNode implements Jst.Parens {
    private final Jst.Expression expression;

    Parens(@Nonnull Jst.Expression expression) {
      this.expression = expression;
    }

    @Nonnull @Override public Jst.Expression getExpression() {
      return expression;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitParens(this);
    }
  }

  private static final class Throw extends AbstractNode implements Jst.Throw {
    private final Jst.Expression expression;

    Throw(@Nonnull Jst.Expression expression) {
      this.expression = expression;
    }

    @Nonnull @Override public Jst.Expression getExpression() {
      return expression;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitThrow(this);
    }
  }

  private static final class Call extends AbstractNode implements Jst.Call {
    private final Jst.Expression methodName;
    private final List<Jst.TypeParameter> typeParameters;
    private final List<Jst.Expression> arguments;

    Call(@Nonnull Jst.Expression methodName,
         @Nonnull Collection<? extends Jst.TypeParameter> typeParameters,
         @Nonnull Collection<? extends Jst.Expression> arguments) {
      this.methodName = methodName;
      this.typeParameters = ImmutableList.copyOf(typeParameters);
      this.arguments = ImmutableList.copyOf(arguments);
    }

    @Nonnull @Override public Jst.Expression getMethodName() {
      return methodName;
    }

    @Nonnull @Override public List<Jst.TypeParameter> getTypeParameters() {
      return typeParameters;
    }

    @Nonnull @Override public List<Jst.Expression> getArguments() {
      return arguments;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitCall(this);
    }
  }

  private static final class NewClass extends AbstractNode implements Jst.NewClass {
    private final Jst.Expression enclosingExpression;
    private final Jst.TypeExpression type;
    private final List<Jst.TypeParameter> typeParameters;
    private final List<Jst.Expression> arguments;
    private final Jst.ClassDeclaration classDeclaration;

    NewClass(@Nullable Jst.Expression enclosingExpression,
             @Nonnull Jst.TypeExpression type,
             @Nonnull Collection<? extends Jst.TypeParameter> typeParameters,
             @Nonnull Collection<? extends Jst.Expression> arguments,
             @Nullable Jst.ClassDeclaration classDeclaration) {
      this.enclosingExpression = enclosingExpression;
      this.type = type;
      this.typeParameters = ImmutableList.copyOf(typeParameters);
      this.arguments = ImmutableList.copyOf(arguments);
      this.classDeclaration = classDeclaration;
    }

    @Nullable @Override public Jst.Expression getEnclosingExpression() {
      return enclosingExpression;
    }

    @Nonnull @Override public Jst.TypeExpression getType() {
      return type;
    }

    @Nonnull @Override public List<Jst.TypeParameter> getTypeParameters() {
      return typeParameters;
    }

    @Nonnull @Override public List<Jst.Expression> getArguments() {
      return arguments;
    }

    @Nullable @Override public Jst.ClassDeclaration getClassDeclaration() {
      return classDeclaration;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitNewClass(this);
    }
  }

  private static final class NewArray extends AbstractNode implements Jst.NewArray {
    private final Jst.TypeExpression type;
    private final List<Jst.Expression> dimensions;
    private final List<Jst.Expression> initializers;

    NewArray(@Nonnull Jst.TypeExpression type,
             @Nonnull Collection<? extends Jst.Expression> dimensions,
             @Nonnull Collection<? extends Jst.Expression> initializers) {
      this.type = type;
      this.dimensions = ImmutableList.copyOf(dimensions);
      this.initializers = ImmutableList.copyOf(initializers);
    }

    @Nonnull @Override public Jst.TypeExpression getType() {
      return type;
    }

    @Nonnull @Override public List<Jst.Expression> getDimensions() {
      return dimensions;
    }

    @Nonnull @Override public List<Jst.Expression> getInitializers() {
      return initializers;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitNewArray(this);
    }
  }

  private static final class EmptyExpression extends AbstractNode implements Jst.EmptyExpression {
    static final EmptyExpression INSTANCE = new EmptyExpression();

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitEmptyExpression(this);
    }
  }

  private static final class EmptyStatement extends AbstractNode implements Jst.EmptyStatement {
    static final EmptyStatement INSTANCE = new EmptyStatement();

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitEmptyStatement(this);
    }
  }

  private static final class Unary extends AbstractNode implements Jst.Unary {
    private final Operator operator;
    private final Jst.Expression expression;

    Unary(@Nonnull Operator operator, @Nonnull Jst.Expression expression) {
      this.operator = operator;
      this.expression = expression;
    }

    @Nonnull @Override public Operator getOperator() {
      return operator;
    }

    @Nonnull @Override public Jst.Expression getExpression() {
      return expression;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitUnary(this);
    }
  }

  private static final class Binary extends AbstractNode implements Jst.Binary {
    private final Operator operator;
    private final Jst.Expression leftExpression;
    private final Jst.Expression rightExpression;

    Binary(@Nonnull Operator operator, @Nonnull Jst.Expression leftExpression, @Nonnull Jst.Expression rightExpression) {
      this.operator = operator;
      this.leftExpression = leftExpression;
      this.rightExpression = rightExpression;
    }

    @Nonnull @Override public Operator getOperator() {
      return operator;
    }

    @Nonnull @Override public Jst.Expression getLeftExpression() {
      return leftExpression;
    }

    @Nonnull @Override public Jst.Expression getRightExpression() {
      return rightExpression;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitBinary(this);
    }
  }

  private static final class Assert extends AbstractNode implements Jst.Assert {
    private final Jst.Expression expression;
    private final Jst.Expression detail;

    Assert(@Nonnull Jst.Expression expression, @Nullable Jst.Expression detail) {
      this.expression = expression;
      this.detail = detail;
    }

    @Nonnull @Override public Jst.Expression getExpression() {
      return expression;
    }

    @Nullable @Override public Jst.Expression getDetail() {
      return detail;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitAssert(this);
    }
  }

  private static final class Assignment extends AbstractNode implements Jst.Assignment {
    private final Jst.Expression leftExpression;
    private final Jst.Expression rightExpression;

    Assignment(@Nonnull Jst.Expression leftExpression, @Nonnull Jst.Expression rightExpression) {
      this.leftExpression = leftExpression;
      this.rightExpression = rightExpression;
    }

    @Nonnull @Override public Jst.Expression getLeftExpression() {
      return leftExpression;
    }

    @Nonnull @Override public Jst.Expression getRightExpression() {
      return rightExpression;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitAssignment(this);
    }
  }

  private static final class CompoundAssignment extends AbstractNode implements Jst.CompoundAssignment {
    private final Operator operator;
    private final Jst.Expression leftExpression;
    private final Jst.Expression rightExpression;

    CompoundAssignment(@Nonnull Operator operator, @Nonnull Jst.Expression leftExpression, @Nonnull Jst.Expression rightExpression) {
      this.operator = operator;
      this.leftExpression = leftExpression;
      this.rightExpression = rightExpression;
    }

    @Nonnull @Override public Jst.Expression getLeftExpression() {
      return leftExpression;
    }

    @Nonnull @Override public Jst.Expression getRightExpression() {
      return rightExpression;
    }

    @Nonnull @Override public Operator getOperator() {
      return operator;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitCompoundAssignment(this);
    }
  }

  private static final class Array extends AbstractNode implements Jst.Array {
    private final Jst.TypeExpression type;

    Array(@Nonnull Jst.TypeExpression type) {
      this.type = type;
    }

    @Nonnull @Override public Jst.TypeExpression getType() {
      return type;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitArray(this);
    }
  }

  private static final class ParameterizedType extends AbstractNode implements Jst.ParameterizedType {
    private final Jst.TypeExpression type;
    private final List<Jst.Expression> arguments;

    ParameterizedType(@Nonnull Jst.TypeExpression type, @Nonnull Collection<? extends Jst.Expression> arguments) {
      this.type = type;
      this.arguments = ImmutableList.copyOf(arguments);
    }

    @Nonnull @Override public Jst.TypeExpression getType() {
      return type;
    }

    @Nonnull @Override public List<Jst.Expression> getArguments() {
      return arguments;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitParameterizedType(this);
    }
  }

  private static final class Wildcard extends AbstractNode implements Jst.Wildcard {
    private final TypeBoundKind kind;
    private final Jst.Expression expression;

    Wildcard(@Nonnull TypeBoundKind kind, @Nullable Jst.Expression expression) {
      this.kind = kind;
      this.expression = expression;
    }

    @Nonnull @Override public TypeBoundKind getKind() {
      return kind;
    }

    @Nullable @Override public Jst.Expression getExpression() {
      return expression;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitWildcard(this);
    }
  }

  private static final class TypeParameter extends AbstractNode implements Jst.TypeParameter {
    private final String name;
    private final List<Jst.Expression> typeBounds;

    TypeParameter(@Nonnull String name, @Nonnull Collection<? extends Jst.Expression> typeBounds) {
      this.name = name;
      this.typeBounds = ImmutableList.copyOf(typeBounds);
    }

    @Nonnull @Override public List<Jst.Expression> getBounds() {
      return typeBounds;
    }

    @Nonnull @Override public String getName() {
      return name;
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitTypeParameter(this);
    }
  }

  private static final class UnionType extends AbstractNode implements Jst.UnionType {
    private final List<Jst.TypeExpression> types;

    UnionType(@Nonnull Collection<? extends Jst.TypeExpression> types) {
      assert types.size() > 1;
      this.types = ImmutableList.copyOf(types);
    }

    @Nonnull @Override public List<Jst.TypeExpression> getTypes() {
      return types;
    }

    @Override
    public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitUnionType(this);
    }
  }
}
