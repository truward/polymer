package com.truward.polymer.code.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.truward.polymer.code.Jst;
import com.truward.polymer.code.JstFlag;
import com.truward.polymer.code.Operator;
import com.truward.polymer.code.factory.JstFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author Alexander Shabanov
 */
public abstract class JstFactorySupport {
  @Nonnull protected abstract JstFactory getFactory();

  @Nonnull public static List<Jst.VarDeclaration> args(@Nonnull Jst.VarDeclaration... vars) {
    return ImmutableList.copyOf(vars);
  }

  @Nonnull public static List<Jst.Statement> statements(@Nonnull Jst.Statement... statements) {
    return ImmutableList.copyOf(statements);
  }

  @Nonnull public static List<Jst.Expression> expressions(@Nonnull Jst.Expression... expressions) {
    return ImmutableList.copyOf(expressions);
  }

  @Nonnull public static List<Jst.TypeExpression> types(@Nonnull Jst.TypeExpression... types) {
    return ImmutableList.copyOf(types);
  }

  @Nonnull public static Set<JstFlag> flags(@Nonnull JstFlag flag) {
    return EnumSet.of(flag);
  }

  @Nonnull public static Set<JstFlag> flags(@Nonnull JstFlag flag1, @Nonnull JstFlag flag2) {
    return EnumSet.of(flag1, flag2);
  }

  @Nonnull public static Set<JstFlag> flags(@Nonnull JstFlag... flags) {
    return flags.length == 0 ? ImmutableSet.<JstFlag>of() : EnumSet.copyOf(Arrays.asList(flags));
  }

  @Nonnull public static List<Jst.Annotation> annotations(@Nonnull Jst.Annotation... annotations) {
    return ImmutableList.copyOf(annotations);
  }

  @Nonnull public Jst.ClassType type(@Nonnull Class<?> wrappedClass) {
    return getFactory().jstClassType(wrappedClass);
  }

  @Nonnull public Jst.Annotation annotation(@Nonnull Class<? extends Annotation> annotationClass) {
    return getFactory().jstAnnotation(type(annotationClass));
  }

  @Nonnull public Jst.Annotation override() {
    return annotation(Override.class);
  }

  @Nonnull public Jst.Block block(@Nonnull Jst.Statement... statements) {
    final Jst.Block block = getFactory().jstBlock();
    block.setStatements(ImmutableList.copyOf(statements));
    return block;
  }

  @Nonnull public Jst.ClassDeclaration classDecl(@Nonnull Collection<JstFlag> flags,
                                                 @Nonnull Collection<Jst.Annotation> annotations,
                                                 @Nonnull String name,
                                                 @Nullable Jst.TypeExpression superclass,
                                                 @Nonnull Collection<Jst.TypeExpression> interfaces,
                                                 @Nonnull Collection<? extends Jst.Statement> statements) {
    final Jst.ClassDeclaration classDeclaration = getFactory().jstClass(name);
    classDeclaration.setFlags(flags);
    classDeclaration.setAnnotations(annotations);
    classDeclaration.setSuperclass(superclass);
    classDeclaration.setInterfaces(interfaces);
    classDeclaration.getBody().setStatements(statements);
    return classDeclaration;
  }
  @Nonnull public Jst.ClassDeclaration classDecl(@Nonnull Collection<JstFlag> flags,
                                                 @Nonnull Collection<Jst.Annotation> annotations,
                                                 @Nonnull String name) {
    return classDecl(flags, annotations, name, null,
        ImmutableList.<Jst.TypeExpression>of(), ImmutableList.<Jst.Statement>of());
  }

  @Nonnull public Jst.VarDeclaration var(@Nonnull Collection<JstFlag> flags,
                                         @Nonnull String fieldName,
                                         @Nonnull Class<?> fieldType,
                                         @Nullable Jst.Expression initializer) {
    final Jst.VarDeclaration var = getFactory().jstVar(fieldName, getFactory().jstClassType(fieldType));
    var.setFlags(flags);
    var.setInitializer(initializer);
    return var;
  }

  @Nonnull public Jst.VarDeclaration var(@Nonnull Collection<JstFlag> flags,
                                         @Nonnull String fieldName,
                                         @Nonnull Class<?> fieldType) {
    return var(flags, fieldName, fieldType, null);
  }

  @Nonnull public Jst.VarDeclaration var(@Nonnull String fieldName,
                                         @Nonnull Class<?> fieldType) {
    return var(ImmutableList.<JstFlag>of(), fieldName, fieldType);
  }

  @Nonnull public Jst.MethodDeclaration method(@Nonnull Collection<JstFlag> flags,
                                               @Nonnull Collection<Jst.Annotation> annotations,
                                               @Nonnull String methodName,
                                               @Nonnull Class<?> returnType,
                                               @Nonnull Collection<Jst.VarDeclaration> arguments,
                                               @Nullable Jst.Block body) {
    final Jst.MethodDeclaration m = getFactory().jstMethod(methodName);
    m.setFlags(flags);
    m.setReturnType(getFactory().jstClassType(returnType));
    m.setArguments(arguments);
    m.setAnnotations(annotations);
    m.setBody(body);
    return m;
  }

  @Nonnull public Jst.MethodDeclaration method(@Nonnull Collection<JstFlag> flags,
                                               @Nonnull String methodName,
                                               @Nonnull Class<?> returnType,
                                               @Nonnull Collection<Jst.VarDeclaration> arguments,
                                               @Nullable Jst.Block body) {
    return method(flags, ImmutableList.<Jst.Annotation>of(), methodName, returnType, arguments, body);
  }

  @Nonnull public Jst.Call call(@Nonnull Jst.Expression methodName,
                                @Nonnull Collection<? extends Jst.Expression> arguments) {
    return getFactory().jstCall(methodName, ImmutableList.<Jst.TypeParameter>of(), arguments);
  }

  @Nonnull public Jst.Binary op(@Nonnull Operator operator, @Nonnull Jst.Expression left, @Nonnull Jst.Expression right) {
    return getFactory().jstBinary(operator, left, right);
  }

  @Nonnull public Jst.Unary op(@Nonnull Operator operator, @Nonnull Jst.Expression expression) {
    return getFactory().jstUnary(operator, expression);
  }

  @Nonnull public Jst.If ifs(@Nonnull Jst.Expression condition, @Nonnull Jst.Statement then, @Nullable Jst.Statement els) {
    return getFactory().jstIf(condition, then, els);
  }

  @Nonnull public Jst.Return returns(@Nullable Jst.Expression expression) {
    return getFactory().jstReturn(expression);
  }

  @Nonnull public Jst.Literal literal(@Nullable Object value) {
    return getFactory().jstLiteral(value);
  }

  @Nonnull public Jst.Expression ident(@Nonnull String first, @Nonnull String... next) {
    Jst.Expression result = getFactory().jstIdentifier(first);

    for (String name : next) {
      result = getFactory().jstSelector(result, name);
    }

    return result;
  }
}
