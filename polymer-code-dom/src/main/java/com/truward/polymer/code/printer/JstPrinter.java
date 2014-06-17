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

  @Nonnull
  private Writer getWriter(@Nonnull Jst.Unit unit) throws IOException {
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

    void print(@Nonnull Jst.Node node) throws IOException {
      node.accept(visitor);
    }

    @Override
    public void visitUnit(@Nonnull Jst.Unit node) throws IOException {
      printer.print("package").print(' ').print(node.getPackageName()).print(';').print('\n');

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

    @Override
    public void visitAnnotation(@Nonnull Jst.Annotation node) throws IOException {
      printer.print('@');
      print(node.getTypeExpression());
      if (!node.getArguments().isEmpty()) {
        printer.print('(');
        printCommaSeparated(node.getArguments());
        printer.print(')');
      }
    }

    @Override
    public void visitClass(@Nonnull Jst.ClassDeclaration node) throws IOException {
      if (node.getFlags().contains(JstFlag.ANONYMOUS)) {
        // special case: anonymous class
        assert node.getSuperclass() == null : "Anonymous class should not extend any class";
        assert node.getInterfaces().isEmpty() : "Anonymous class should not implement any interfaces";
        assert node.getTypeParameters().isEmpty() : "Anonymous class should not be parameterized";
      } else {
        printer.print('\n');
        printNamedStatement(node, false);
        if (node.getFlags().contains(JstFlag.INTERFACE)) {
          printer.print("interface");
        } else if (node.getFlags().contains(JstFlag.ENUM)) {
          printer.print("enum");
        } else {
          printer.print("class");
        }

        printer.print(' ').print(node.getName()).print(' ');

        // print type parameters
        if (!node.getTypeParameters().isEmpty()) {
          printTypeParameters(node.getTypeParameters());
          printer.print(' ');
        }

        // print superclass
        final Jst.TypeExpression superclass = node.getSuperclass();
        if (superclass != null) {
          assert !node.getFlags().contains(JstFlag.INTERFACE) : "Interface should not have superclass";
          printer.print("extends").print(' ');
          print(superclass);
          printer.print(' ');
        }

        // print interfaces
        if (!node.getInterfaces().isEmpty()) {
          if (node.getFlags().contains(JstFlag.INTERFACE)) {
            printer.print("extends");
          } else {
            printer.print("implements");
          }
          printer.print(' ');
          printCommaSeparated(node.getInterfaces());
          printer.print(' ');
        }
      }

      print(node.getBody());
    }

    @Override
    public void visitMethod(@Nonnull Jst.MethodDeclaration node) throws IOException {
      printer.print('\n').print('\n');
      printNamedStatement(node, false);
      if (!node.getTypeParameters().isEmpty()) {
        printTypeParameters(node.getTypeParameters());
      }
      print(node.getReturnType());
      printer.print(' ').print(node.getName());
      printer.print('(');
      printCommaSeparated(node.getArguments());
      printer.print(')');

      final Jst.Block body = node.getBody();
      if (body != null) {
        printer.print(' ');
        print(body);
      } else {
        printer.print(';');
      }
    }

    @Override
    public void visitVar(@Nonnull Jst.VarDeclaration node) throws IOException {
      boolean isInBlock = parents.getParent() instanceof Jst.Block; // parent is a block (variable or field)

      printNamedStatement(node, !isInBlock); // inline annotations if not in block (e.g. for arguments)
      print(node.getType());
      printer.print(' ').print(node.getName());

      final Jst.Expression initializer = node.getInitializer();
      if (initializer != null) {
        printer.print(' ').print('=').print(' ');
        print(initializer);
      }

      if (isInBlock) {
        printer.print(';');
      }
    }

    @Override
    public void visitReturn(@Nonnull Jst.Return node) throws IOException {
      printer.print("return");
      final Jst.Expression expression = node.getExpression();
      if (expression != null) {
        printer.print(' ');
        print(expression);
      }
      printer.print(';');
    }

    @Override
    public void visitLiteral(@Nonnull Jst.Literal node) throws IOException {
      printLiteral(node.getValue());
    }

    @Override
    public void visitIdentifier(@Nonnull Jst.Identifier node) throws IOException {
      printer.print(node.getName());
    }

    @Override
    public void visitSelector(@Nonnull Jst.Selector node) throws IOException {
      print(node.getExpression());
      printer.print('.').print(node.getName());
    }

    @Override
    public void visitBlock(@Nonnull Jst.Block node) throws IOException {
      printer.print('{');
      printStatements(node.getStatements());
      printer.print('}');
    }

    @Override
    public void visitAssignment(@Nonnull Jst.Assignment node) throws IOException {
      print(node.getLeftExpression());
      printer.print(' ').print('=').print(' ');
      print(node.getRightExpression());
    }

    @Override
    public void visitCompoundAssignment(@Nonnull Jst.CompoundAssignment node) throws IOException {
      print(node.getLeftExpression());
      printer.print(' ');
      printOperator(node.getOperator());
      printer.print('=').print(' ');
      print(node.getRightExpression());
    }

    @Override
    public void visitExpressionStatement(@Nonnull Jst.ExpressionStatement node) throws IOException {
      boolean isInBlock = parents.getParent() instanceof Jst.Block;
      print(node.getExpression());
      if (isInBlock) {
        printer.print(';');
      }
    }

    @Override
    public void visitSimpleClass(@Nonnull Jst.SimpleClassType node) throws IOException {
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
          printer.print('.');
          printTypeParameters(node.getTypeParameters());
          printer.print(selectorMethodName.getName());
        }
      }

      // print arguments
      printer.print('(');
      printCommaSeparated(node.getArguments());
      printer.print(')');
    }

    @Override public void visitEmptyExpression(@Nonnull Jst.EmptyExpression node) throws IOException {
      // do nothing
    }

    @Override public void visitEmptyStatement(@Nonnull Jst.EmptyStatement node) throws IOException {
      // do nothing
    }

    @Override public void visitImport(@Nonnull Jst.Import node) throws IOException {
      printer.print("import").print(' ');
      if (node.isStatic()) {
        printer.print("static").print(' ');
      }
      printer.print(node.getImportName()).print(';');
    }

    @Override public void visitIf(@Nonnull Jst.If node) throws IOException {
      printer.print("if").print(' ').print('(');
      print(node.getCondition());
      printer.print(')').print(' ');
      print(node.getThenPart());
      // optional else part
      final Jst.Statement elsePart = node.getElsePart();
      if (elsePart != null) {
        printer.print(' ').print("else").print(' ');
        print(elsePart);
      }
    }

    @Override public void visitContinue(@Nonnull Jst.Continue node) throws IOException {
      printer.print("continue");
      final String label = node.getLabel();
      if (label != null) {
        printer.print(' ').print(label);
      }
      printer.print(';');
    }

    @Override public void visitBreak(@Nonnull Jst.Break node) throws IOException {
      printer.print("break");
      final String label = node.getLabel();
      if (label != null) {
        printer.print(' ').print(label);
      }
      printer.print(';');
    }

    @Override public void visitThrow(@Nonnull Jst.Throw node) throws IOException {
      printer.print("throw").print(' ');
      print(node.getExpression());
      printer.print(';');
    }

    @Override public void visitUnary(@Nonnull Jst.Unary node) throws IOException {
      printOperator(node.getOperator());
      print(node.getExpression());
    }

    @Override public void visitBinary(@Nonnull Jst.Binary node) throws IOException {
      print(node.getLeftExpression());
      printer.print(' ');
      printOperator(node.getOperator());
      printer.print(' ');
      print(node.getRightExpression());
    }

    @Override public void visitInstanceOf(@Nonnull Jst.InstanceOf node) throws IOException {
      print(node.getExpression());
      printer.print(' ').print("instanceof").print(' ');
      print(node.getType());
    }

    @Override public void visitInitializerBlock(@Nonnull Jst.InitializerBlock node) throws IOException {
      if (node.isStatic()) {
        printer.print("static").print(' ');
      }
      printer.print('{');
      printStatements(node.getStatements());
      printer.print('}');
    }

    @Override public void visitWhileLoop(@Nonnull Jst.WhileLoop node) throws IOException {
      printer.print("while").print(' ').print('(');
      print(node.getCondition());
      printer.print(')').print(' ');
      print(node.getBody());
    }

    @Override public void visitDoWhileLoop(@Nonnull Jst.DoWhileLoop node) throws IOException {
      printer.print("do").print(' ');
      print(node.getBody());
      printer.print(' ').print("while").print('(');
      print(node.getCondition());
      printer.print(')').print(';');
    }

    @Override public void visitParens(@Nonnull Jst.Parens node) throws IOException {
      printer.print('(');
      print(node.getExpression());
      printer.print(')');
    }

    //
    // Private
    //

    private void printTypeParameters(@Nonnull List<Jst.TypeParameter> typeParameters) throws IOException {
      printer.print('<');
      printCommaSeparated(typeParameters);
      printer.print('>');
    }

    private void printCommaSeparated(@Nonnull List<? extends Jst.Node> nodes) throws IOException {
      final int size = nodes.size();
      for (int i = 0; i < size; ++i) {
        if (i > 0) {
          printer.print(',').print(' ');
        }
        print(nodes.get(i));
      }
    }

    private void printNamedStatement(@Nonnull Jst.NamedStatement namedStatement, boolean inlineAnnotations) throws IOException {
      final char annotationSeparatorChar = inlineAnnotations ? ' ' : '\n';
      for (final Jst.Annotation annotation : namedStatement.getAnnotations()) {
        visitAnnotation(annotation);
        printer.print(annotationSeparatorChar);
      }

      for (final JstFlag flag : namedStatement.getFlags()) {
        if (!flag.isModifier()) {
          continue;
        }
        printer.print(flag.toString()).print(' ');
      }
    }

    private void printStatements(@Nonnull List<Jst.Statement> statements) throws IOException {
      for (final Jst.Statement statement : statements) {
        print(statement);
      }
    }

    private void printLiteral(@Nullable Object value) throws IOException {
      if (value == null) {
        printer.print("null");
      } else if (value instanceof String) {
        printer.print('\"');
        printer.print(EscapeUtil.escape((String) value));
        printer.print('\"');
      } else if (value instanceof Character) {
        printer.print('\'');
        printer.print(EscapeUtil.escape((Character) value));
        printer.print('\'');
      } else if (value instanceof Boolean) {
        printer.print(value.toString());
      } else if (value instanceof Number) {
        printer.print(value.toString());
        // suffix
        if (value instanceof Double) {
          printer.print('D');
        } else if (value instanceof Float) {
          printer.print('F');
        } else if (value instanceof Long) {
          printer.print('L');
        }
      } else {
        throw new IllegalArgumentException("Unknown literal: " + value);
      }
    }

    private void printOperator(@Nonnull Operator operator) throws IOException {
      printer.print(operator.getValue());
    }

    private void printClassNameReference(@Nonnull FqName className) throws IOException {
      if (!className.isRoot() && className.getParent().equals(JAVA_LANG_PACKAGE)) {
        // standard JDK class from java.lang
        printer.print(className.getName());
        return;
      }

      printer.print(className);
    }
  }
}
