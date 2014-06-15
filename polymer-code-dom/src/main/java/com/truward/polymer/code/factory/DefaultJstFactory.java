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
public class DefaultJstFactory implements JstFactory {
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
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.EmptyExpression jstEmptyExpression() {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Block jstBlock() {
    return new Block();
  }

  @Nonnull @Override public Jst.InitializerBlock jstInitializerBlock(boolean isStatic) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.DoWhileLoop jstDoWhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.WhileLoop jstWhileLoop(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.ForLoop jstForLoop(@Nonnull List<Jst.Statement> initializers,
                                                   @Nonnull Jst.Expression condition,
                                                   @Nonnull Jst.Expression step,
                                                   @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.ForEachLoop jstForEachLoop(@Nonnull Jst.VarDeclaration var,
                                                           @Nonnull Jst.Expression expression,
                                                           @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Labeled jstLabeled(@Nonnull String label, @Nonnull Jst.Statement body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Switch jstSwitch(@Nonnull Jst.Expression selector, @Nonnull List<Jst.Case> cases) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Synchronized jstSynchronized(@Nonnull Jst.Expression lock, @Nonnull Jst.Block body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Try jstTry(@Nonnull Jst.Block body,
                                           @Nonnull List<Jst.Catch> catchers,
                                           @Nullable Jst.Block finalizer) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Catch jstCatch(@Nonnull Jst.VarDeclaration parameter, @Nonnull Jst.Block body) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.If jstIf(@Nonnull Jst.Expression condition,
                                         @Nonnull Jst.Statement then,
                                         @Nullable Jst.Statement els) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Conditional jstConditional(@Nonnull Jst.Expression condition,
                                                           @Nonnull Jst.Expression thenPart,
                                                           @Nonnull Jst.Expression elsePart) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Break jstBreak(@Nullable String label) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Continue jstContinue(@Nullable String label) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Return jstReturn(@Nullable Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Throw jstThrow(@Nonnull Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Assert jstAssert(@Nonnull Jst.Expression expression,
                                                 @Nullable Jst.Expression detail) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Call jstCall(@Nonnull String methodName,
                                             @Nullable Jst.Expression base,
                                             @Nonnull List<Jst.TypeParameter> typeParameters,
                                             @Nonnull List<Jst.Expression> arguments) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.NewClass jstNewClass(@Nullable Jst.Expression enclosingExpression,
                                                     @Nonnull Jst.TypeExpression type,
                                                     @Nonnull List<Jst.TypeParameter> typeParameters,
                                                     @Nonnull List<Jst.Expression> arguments,
                                                     @Nullable Jst.ClassDeclaration classDeclaration) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.NewArray jstNewArray(@Nonnull Jst.TypeExpression type,
                                                     @Nonnull List<Jst.Expression> dimensions,
                                                     @Nonnull List<Jst.Expression> initializers) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Parens jstParens(@Nonnull Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Assignment jstAssignment(@Nonnull Jst.Expression left, @Nonnull Jst.Expression right) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.CompoundAssignment jstCompoundAssignment(@Nonnull Operator operator,
                                                                         @Nonnull Jst.Expression left,
                                                                         @Nonnull Jst.Expression right) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Unary jstUnary(@Nonnull Operator operator, @Nonnull Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Binary jstBinary(@Nonnull Operator operator,
                                                 @Nonnull Jst.Expression left,
                                                 @Nonnull Jst.Expression right) {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.ParameterizedType jstParameterizedType(@Nonnull Jst.TypeExpression type,
                                                                       @Nonnull List<Jst.Expression> arguments) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.Wildcard jstWildcard(@Nullable Jst.TypeBoundExpression typeBoundExpression) {
    throw new UnsupportedOperationException();
  }

  @Nonnull @Override public Jst.TypeBoundExpression jstTypeBound(@Nonnull TypeBoundKind kind,
                                                                 @Nonnull Jst.Expression expression) {
    throw new UnsupportedOperationException();
  }


  //
  // Private
  //


  private static abstract class AbstractNode implements Jst.Node {

    @Override public String toString() {
      return "Jst." + getClass().getSimpleName() + "#" + hashCode();
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

  private static final class Block extends AbstractNode implements Jst.Block {
    private List<Jst.Statement> statements = ImmutableList.of();

    @Nonnull @Override public List<Jst.Statement> getStatements() {
      return statements;
    }

    @Override public void setStatements(@Nonnull List<Jst.Statement> statements) {
      this.statements = ImmutableList.copyOf(statements);
    }

    @Override public <E extends Exception> void accept(@Nonnull JstVisitor<E> visitor) throws E {
      visitor.visitBlock(this);
    }
  }

  private static abstract class NamedStatement extends AbstractNode implements Jst.NamedStatement {
    private final String name;
    private List<Jst.Annotation> annotations = ImmutableList.of();
    private Set<JstFlag> flags = ImmutableSet.of();

    NamedStatement(@Nonnull String name) {
      this.name = name;
    }

    @Nonnull @Override public final String getName() {
      return name;
    }

    @Nonnull @Override public final List<Jst.Annotation> getAnnotations() {
      return annotations;
    }

    @Override public final void setAnnotations(@Nonnull Collection<? extends Jst.Annotation> annotations) {
      this.annotations = ImmutableList.copyOf(annotations);
    }

    @Nonnull @Override public final Set<JstFlag> getFlags() {
      return flags;
    }

    @Override public final void setFlags(@Nonnull Collection<JstFlag> flags) {
      this.flags = EnumSet.copyOf(flags);
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

    @Override public void setSuperclass(@Nonnull Jst.TypeExpression superclass) {
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

    MethodDeclaration(@Nonnull String name, @Nonnull Jst.TypeExpression defaultReturnType) {
      super(name);
      this.returnType = defaultReturnType;
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

    @Override public void setArguments(@Nonnull List<Jst.VarDeclaration> arguments) {
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
}
