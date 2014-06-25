package com.truward.polymer.code.printer;

import com.truward.polymer.code.Jst;
import com.truward.polymer.code.JstFlag;
import com.truward.polymer.code.Operator;
import com.truward.polymer.code.visitor.JstVisitor;
import com.truward.polymer.code.visitor.parent.JstParentAwareVisitor;
import com.truward.polymer.code.visitor.parent.ParentManager;
import com.truward.polymer.code.visitor.parent.ParentProvider;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.OutputStreamProvider;
import com.truward.polymer.output.StandardFileType;
import com.truward.polymer.printer.CAlikePrinter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Class, responsible for printing java compilation units into the output stream provider.
 *
 * @author Alexander Shabanov
 */
public final class JstPrinter {
  private final OutputStreamProvider provider;

  public JstPrinter(@Nonnull OutputStreamProvider provider) {
    this.provider = provider;
  }

  public void print(@Nonnull Jst.Unit unit) throws IOException {
    try (final Writer writer = getWriter(unit)) {
      final CAlikePrinter printer = new CAlikePrinter(writer);
      final ParentManager parents = new ParentManager();

      // create parent aware and print visitors
      final PrintVisitor printVisitor = new PrintVisitor(parents, printer);
      final JstParentAwareVisitor<IOException> parentAwareVisitor = new JstParentAwareVisitor<>(printVisitor, parents);
      printVisitor.setVisitor(parentAwareVisitor);

      unit.accept(parentAwareVisitor);
    }
  }

  //
  // Private
  //

  @Nonnull private Writer getWriter(@Nonnull Jst.Unit unit) throws IOException {
    FqName result = unit.getPackageName();

    if (!unit.getClasses().isEmpty()) {
      // unit contains classes - use very first class to identify file name
      // TODO: this won't work properly if first class is package local and one of the next classes defined in this unit is public
      result = new FqName(unit.getClasses().get(0).getName(), result);
    }

    return new OutputStreamWriter(provider.createStreamForFile(result, StandardFileType.JAVA), StandardCharsets.UTF_8);
  }

  private static final class PrintVisitor extends JstVisitor<IOException> {
    private static final FqName JAVA_LANG_PACKAGE = FqName.valueOf("java.lang");

    private final ParentProvider parents;
    private final CAlikePrinter printer;
    private JstVisitor<IOException> visitor;

    void setVisitor(@Nonnull JstVisitor<IOException> visitor) {
      this.visitor = visitor;
    }

    PrintVisitor(@Nonnull ParentProvider parents, @Nonnull CAlikePrinter printer) {
      this.parents = parents;
      this.printer = printer;
      setVisitor(this);
    }

    @Nonnull PrintVisitor print(@Nonnull Jst.Node node) throws IOException {
      node.accept(visitor);
      return this;
    }
    
    @Nonnull PrintVisitor print(char ch) throws IOException {
      printer.print(ch);
      return this;
    }
    
    @Nonnull PrintVisitor print(@Nonnull String str) throws IOException {
      printer.print(str);
      return this;
    }
    
    @Nonnull PrintVisitor print(@Nonnull FqName fqName) throws IOException {
      printer.print(fqName);
      return this;
    }

    @Override public void visitUnit(@Nonnull Jst.Unit node) throws IOException {
      if (!node.getAnnotations().isEmpty()) {
        printSeparated(node.getAnnotations(), "\n");
      }
      print("package").print(' ').print(node.getPackageName()).print(';').print('\n');

      if (!node.getImports().isEmpty()) {
        for (final Jst.Import importNode : node.getImports()) {
          visitImport(importNode);
        }
      }

      if (!node.getClasses().isEmpty()) {
        for (final Jst.ClassDeclaration classDeclaration : node.getClasses()) {
          visitClass(classDeclaration);
        }
      }
    }

    @Override public void visitAnnotation(@Nonnull Jst.Annotation node) throws IOException {
      print('@').print(node.getTypeExpression());
      if (!node.getArguments().isEmpty()) {
        print('(').printCommaSeparated(node.getArguments()).print(')');
      }
    }

    @Override public void visitClass(@Nonnull Jst.ClassDeclaration node) throws IOException {
      if (node.getFlags().contains(JstFlag.ANONYMOUS)) {
        // special case: anonymous class
        assert node.getSuperclass() == null : "Anonymous class should not extend any class";
        assert node.getInterfaces().isEmpty() : "Anonymous class should not implement any interfaces";
        assert node.getTypeParameters().isEmpty() : "Anonymous class should not be parameterized";
      } else {
        print('\n');
        printNamedStatement(node, false);
        if (node.getFlags().contains(JstFlag.INTERFACE)) {
          print("interface");
        } else if (node.getFlags().contains(JstFlag.ENUM)) {
          print("enum");
        } else if (node.getFlags().contains(JstFlag.ANNOTATION)) {
          print("@interface");
        } else {
          print("class");
        }

        print(' ').print(node.getName()).print(' ');

        // print type parameters
        if (!node.getTypeParameters().isEmpty()) {
          printTypeParameters(node.getTypeParameters()).print(' ');
        }

        // print superclass
        final Jst.TypeExpression superclass = node.getSuperclass();
        if (superclass != null) {
          assert !node.getFlags().contains(JstFlag.INTERFACE) : "Interface should not have superclass";
          print("extends").print(' ').print(superclass).print(' ');
        }

        // print interfaces
        if (!node.getInterfaces().isEmpty()) {
          if (node.getFlags().contains(JstFlag.INTERFACE)) {
            print("extends");
          } else {
            print("implements");
          }
          print(' ').printCommaSeparated(node.getInterfaces()).print(' ');
        }
      }

      print(node.getBody());
    }

    @Override public void visitMethod(@Nonnull Jst.MethodDeclaration node) throws IOException {
      print('\n').print('\n');
      printNamedStatement(node, false);
      if (!node.getTypeParameters().isEmpty()) {
        printTypeParameters(node.getTypeParameters());
      }
      print(node.getReturnType()).print(' ').print(node.getName());
      print('(').printCommaSeparated(node.getArguments()).print(')');

      if (!node.getThrown().isEmpty()) {
        print(' ').print("throws").print(' ').printCommaSeparated(node.getThrown());
      }

      final Jst.Expression defaultValue = node.getDefaultValue();
      if (defaultValue != null) {
        print(' ').print('=').print(' ').print(defaultValue); // annotation method
      }

      final Jst.Block body = node.getBody();
      if (body != null) {
        print(' ').print(body);
      } else {
        print(';');
      }
    }

    @Override public void visitVar(@Nonnull Jst.VarDeclaration node) throws IOException {
      boolean isInBlock = parents.getParent() instanceof Jst.Block; // parent is a block (variable or field)

      printNamedStatement(node, !isInBlock); // inline annotations if not in block (e.g. for arguments)
      print(node.getType()).print(' ').print(node.getName());

      final Jst.Expression initializer = node.getInitializer();
      if (initializer != null) {
        print(' ').print('=').print(' ').print(initializer);
      }

      if (isInBlock) {
        print(';');
      }
    }

    @Override public void visitReturn(@Nonnull Jst.Return node) throws IOException {
      print("return");
      final Jst.Expression expression = node.getExpression();
      if (expression != null) {
        print(' ').print(expression);
      }
      print(';');
    }

    @Override public void visitLiteral(@Nonnull Jst.Literal node) throws IOException {
      printLiteral(node.getValue());
    }

    @Override public void visitIdentifier(@Nonnull Jst.Identifier node) throws IOException {
      print(node.getName());
    }

    @Override public void visitSelector(@Nonnull Jst.Selector node) throws IOException {
      print(node.getExpression()).print('.').print(node.getName());
    }

    @Override public void visitBlock(@Nonnull Jst.Block node) throws IOException {
      print('{').printStatements(node.getStatements()).print('}');
    }

    @Override public void visitAssignment(@Nonnull Jst.Assignment node) throws IOException {
      print(node.getLeftExpression()).print(' ').print('=').print(' ').print(node.getRightExpression());
    }

    @Override public void visitCompoundAssignment(@Nonnull Jst.CompoundAssignment node) throws IOException {
      print(node.getLeftExpression()).print(' ').printOperator(node.getOperator()).print('=').print(' ');
      print(node.getRightExpression());
    }

    @Override public void visitExpressionStatement(@Nonnull Jst.ExpressionStatement node) throws IOException {
      boolean isInBlock = parents.getParent() instanceof Jst.Block;
      print(node.getExpression());
      if (isInBlock) {
        print(';');
      }
    }

    @Override public void visitSimpleClass(@Nonnull Jst.SimpleClassType node) throws IOException {
      printClassNameReference(node.getFqName());
    }

    @Override public void visitCall(@Nonnull Jst.Call node) throws IOException {
      final Jst.Expression methodName = node.getMethodName();
      if (node.getTypeParameters().isEmpty()) {
        print(methodName);
      } else {
        // special case: print type parameters with method name
        if (!(methodName instanceof Jst.Selector)) {
          // print type parameters before method name, like <MyType>copyOf(something)
          printTypeParameters(node.getTypeParameters());
          print(methodName);
        } else {
          // print type parameters before last name in a selector, like ImmutableList.<MyType>copyOf(something)
          final Jst.Selector selectorMethodName = (Jst.Selector) methodName;
          print(selectorMethodName.getExpression());
          print('.');
          printTypeParameters(node.getTypeParameters());
          print(selectorMethodName.getName());
        }
      }

      // print arguments
      print('(').printCommaSeparated(node.getArguments()).print(')');
    }

    @Override public void visitEmptyExpression(@Nonnull Jst.EmptyExpression node) throws IOException {
      // do nothing
    }

    @Override public void visitEmptyStatement(@Nonnull Jst.EmptyStatement node) throws IOException {
      // do nothing
    }

    @Override public void visitImport(@Nonnull Jst.Import node) throws IOException {
      print("import").print(' ');
      if (node.isStatic()) {
        print("static").print(' ');
      }
      print(node.getImportName()).print(';');
    }

    @Override public void visitIf(@Nonnull Jst.If node) throws IOException {
      print("if").print(' ').print('(').print(node.getCondition()).print(')').print(' ').print(node.getThenPart());
      // optional else part
      final Jst.Statement elsePart = node.getElsePart();
      if (elsePart != null) {
        print(' ').print("else").print(' ');
        print(elsePart);
      }
    }

    @Override public void visitContinue(@Nonnull Jst.Continue node) throws IOException {
      print("continue");
      final String label = node.getLabel();
      if (label != null) {
        print(' ').print(label);
      }
      print(';');
    }

    @Override public void visitBreak(@Nonnull Jst.Break node) throws IOException {
      print("break");
      final String label = node.getLabel();
      if (label != null) {
        print(' ').print(label);
      }
      print(';');
    }

    @Override public void visitThrow(@Nonnull Jst.Throw node) throws IOException {
      print("throw").print(' ').print(node.getExpression()).print(';');
    }

    @Override public void visitUnary(@Nonnull Jst.Unary node) throws IOException {
      printOperator(node.getOperator()).print(node.getExpression());
    }

    @Override public void visitBinary(@Nonnull Jst.Binary node) throws IOException {
      print(node.getLeftExpression()).print(' ').printOperator(node.getOperator()).print(' ').print(node.getRightExpression());
    }

    @Override public void visitInstanceOf(@Nonnull Jst.InstanceOf node) throws IOException {
      print(node.getExpression()).print(' ').print("instanceof").print(' ').print(node.getType());
    }

    @Override public void visitInitializerBlock(@Nonnull Jst.InitializerBlock node) throws IOException {
      if (node.isStatic()) {
        print("static").print(' ');
      }
      print('{').printStatements(node.getStatements()).print('}');
    }

    @Override public void visitWhileLoop(@Nonnull Jst.WhileLoop node) throws IOException {
      print("while").print(' ').print('(').print(node.getCondition()).print(')').print(' ').print(node.getBody());
    }

    @Override public void visitDoWhileLoop(@Nonnull Jst.DoWhileLoop node) throws IOException {
      print("do").print(' ').print(node.getBody()).print(' ').print("while").print('(').print(node.getCondition());
      print(')').print(';');
    }

    @Override public void visitParens(@Nonnull Jst.Parens node) throws IOException {
      print('(').print(node.getExpression()).print(')');
    }

    @Override public void visitForLoop(@Nonnull Jst.ForLoop node) throws IOException {
      print("for").print(' ').print('(').printCommaSeparated(node.getInitializers());
      print("; ").print(node.getCondition());
      print("; ").print(node.getStep());
      print(')').print(' ').print(node.getBody());
    }

    @Override public void visitForEachLoop(@Nonnull Jst.ForEachLoop node) throws IOException {
      print("for").print(' ').print('(').print(node.getVariable()).print(' ').print(':').print(' ');
      print(node.getExpression());
      print(')').print(' ');
      print(node.getBody());
    }

    @Override public void visitLabeled(@Nonnull Jst.Labeled node) throws IOException {
      print(node.getLabel()).print(':');
      print(node.getBody());
    }

    @Override public void visitSwitch(@Nonnull Jst.Switch node) throws IOException {
      print("switch").print('(').print(node.getSelector()).print(')').print(' ').print('{');
      for (final Jst.Case c : node.getCases()) {
        print(c);
      }
      print('}');
    }

    @Override
    public void visitCase(@Nonnull Jst.Case node) throws IOException {
      print('\n');

      final Jst.Expression expression = node.getExpression();
      if (expression != null) {
        print("case").print(' ').print(expression);
      } else {
        print("default");
      }
      print(':').print('\n');

      for (final Jst.Statement statement : node.getStatements()) {
        print(statement);
      }
    }

    @Override public void visitSynchronized(@Nonnull Jst.Synchronized node) throws IOException {
      print("synchronized").print(' ').print('(').print(node.getLock()).print(')').print(' ').print(node.getBody());
    }

    @Override public void visitTry(@Nonnull Jst.Try node) throws IOException {
      print("try").print(' ').print(node.getBody());
      for (final Jst.Catch catcher : node.getCatchers()) {
        print(catcher);
      }
      final Jst.Block finalizer = node.getFinalizer();
      if (finalizer != null) {
        print("finally").print(' ').print(finalizer);
      }
    }

    @Override public void visitCatch(@Nonnull Jst.Catch node) throws IOException {
      print("catch").print(' ').print('(').print(node.getParameter()).print(')').print(' ').print(node.getBody());
    }

    @Override public void visitConditional(@Nonnull Jst.Conditional node) throws IOException {
      print(node.getCondition()).print(' ').print('?').print(' ');
      print(node.getThenPart()).print(' ').print(':').print(' ').print(node.getElsePart());
    }

    @Override public void visitAssert(@Nonnull Jst.Assert node) throws IOException {
      print("assert").print(' ').print(node.getExpression());
      final Jst.Expression detail = node.getDetail();
      if (detail != null) {
        print(' ').print(':').print(' ').print(detail);
      }
      print(';');
    }

    @Override public void visitWildcard(@Nonnull Jst.Wildcard node) throws IOException {
      print('?');
      final Jst.Expression expression = node.getExpression();
      switch (node.getKind()) {
        case UNBOUND:
          assert expression == null : "Unbound type expression should be null";
          return;

        case SUPER:
          print(' ').print("extends").print(' ');
          break;

        case EXTENDS:
          print(' ').print("super").print(' ');
          break;

        default:
          throw new UnsupportedOperationException("Unsupported bound kind = " + node.getKind());
      }
      assert expression != null : "Bound expression should not be null";
      print(expression);
    }

    @Override public void visitParameterizedType(@Nonnull Jst.ParameterizedType node) throws IOException {
      print(node.getType()).print('<').printCommaSeparated(node.getArguments()).print('>');
    }

    @Override public void visitUnionType(@Nonnull Jst.UnionType node) throws IOException {
      assert node.getTypes().size() > 1 : "Union types should be represented with a list of at least two elements";
      printSeparated(node.getTypes(), " | ");
    }

    @Override public void visitTypeParameter(@Nonnull Jst.TypeParameter node) throws IOException {
      print(node.getName());
      if (node.getBounds().isEmpty()) {
        return;
      }

      print(' ').print("extends").print(' ');
      final int count = node.getBounds().size();
      for (int i = 0; i < count; ++i) {
        if (i > 0) {
          print(' ').print('&').print(' ');
        }
        print(node.getBounds().get(i));
      }
    }

    @Override public void visitArray(@Nonnull Jst.Array node) throws IOException {
      print(node.getType());
      print('[').print(']');
    }

    @Override public void visitTypeCast(@Nonnull Jst.TypeCast node) throws IOException {
      print('(').print(node.getType()).print(')').print(' ').print(node.getExpression());
    }

    @Override public void visitArrayAccess(@Nonnull Jst.ArrayAccess node) throws IOException {
      print(node.getExpression()).print('[').print(node.getIndex()).print(']');
    }

    @Override public void visitNewArray(@Nonnull Jst.NewArray node) throws IOException {
      final Jst.TypeExpression type = node.getType();
      if (type != null) {
        print("new").print(' ').print(type);
        for (final Jst.Expression expression : node.getDimensions()) {
          print('[').print(expression).print(']');
        }
        assert node.getInitializers().isEmpty();
      }

      if (!node.getInitializers().isEmpty()) {
        // special case: inline array initializer, curly braces should be printed as string
        print("{").printCommaSeparated(node.getInitializers()).print("}");
      }
    }

    @Override public void visitNewClass(@Nonnull Jst.NewClass node) throws IOException {
      final Jst.Expression enclosingExpression = node.getEnclosingExpression();
      if (enclosingExpression != null) {
        print(enclosingExpression);
      }
      print('.').print(' ').print("new").print(' ').print(node.getType());
      print('(').printCommaSeparated(node.getArguments()).print(')');

      final Jst.Block classDeclaration = node.getClassDeclaration();
      if (classDeclaration != null) {
        print(' ').print(classDeclaration);
      }
    }

    //
    // Private
    //

    @Nonnull private PrintVisitor printTypeParameters(@Nonnull List<Jst.TypeParameter> typeParameters) throws IOException {
      print('<').printCommaSeparated(typeParameters).print('>');
      return this;
    }

    @Nonnull private PrintVisitor printCommaSeparated(@Nonnull List<? extends Jst.Node> nodes) throws IOException {
      return printSeparated(nodes, ", ");
    }

    @Nonnull private PrintVisitor printSeparated(@Nonnull List<? extends Jst.Node> nodes,
                                                 @Nonnull CharSequence separator) throws IOException {
      final int separatorSize = separator.length();
      final int size = nodes.size();
      for (int i = 0; i < size; ++i) {
        if (i > 0) {
          for (int j = 0; j < separatorSize; ++j) {
            print(separator.charAt(j));
          }
        }
        print(nodes.get(i));
      }
      return this;
    }

    @Nonnull private PrintVisitor printNamedStatement(@Nonnull Jst.NamedStatement namedStatement, boolean inlineAnnotations) throws IOException {
      final char annotationSeparatorChar = inlineAnnotations ? ' ' : '\n';
      for (final Jst.Annotation annotation : namedStatement.getAnnotations()) {
        visitAnnotation(annotation);
        print(annotationSeparatorChar);
      }

      for (final JstFlag flag : namedStatement.getFlags()) {
        if (!flag.isModifier()) {
          continue;
        }
        print(flag.toString()).print(' ');
      }
      return this;
    }

    @Nonnull private PrintVisitor printStatements(@Nonnull List<Jst.Statement> statements) throws IOException {
      for (final Jst.Statement statement : statements) {
        print(statement);
      }
      return this;
    }

    @Nonnull private PrintVisitor printLiteral(@Nullable Object value) throws IOException {
      if (value == null) {
        print("null");
      } else if (value instanceof String) {
        print('\"');
        print(EscapeUtil.escape((String) value));
        print('\"');
      } else if (value instanceof Character) {
        print('\'');
        print(EscapeUtil.escape((Character) value));
        print('\'');
      } else if (value instanceof Boolean) {
        print(value.toString());
      } else if (value instanceof Number) {
        print(value.toString());
        // suffix
        if (value instanceof Double) {
          print('D');
        } else if (value instanceof Float) {
          print('F');
        } else if (value instanceof Long) {
          print('L');
        }
      } else if (value instanceof Enum) {
        final Enum<?> e = (Enum<?>) value;
        printClassNameReference(FqName.valueOf(value.getClass().getName())).print('.').print(e.name());
      } else {
        throw new IllegalArgumentException("Unknown literal: " + value);
      }

      return this;
    }

    @Nonnull private PrintVisitor printOperator(@Nonnull Operator operator) throws IOException {
      return print(operator.getValue());
    }

    @Nonnull private PrintVisitor printClassNameReference(@Nonnull FqName className) throws IOException {
      if (!className.isRoot() && className.getParent().equals(JAVA_LANG_PACKAGE)) {
        // standard JDK class from java.lang
        print(className.getName());
      } else {
        print(className);
      }
      return this;
    }
  }
}
