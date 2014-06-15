package com.truward.polymer.code.printer;

import com.truward.polymer.code.Jst;
import com.truward.polymer.code.JstFlag;
import com.truward.polymer.code.visitor.JstVisitor;
import com.truward.polymer.code.visitor.parent.JstParentAwareVisitor;
import com.truward.polymer.code.visitor.parent.ParentManager;
import com.truward.polymer.code.visitor.parent.ParentProvider;
import com.truward.polymer.naming.FqName;
import com.truward.polymer.output.OutputStreamProvider;
import com.truward.polymer.output.StandardFileType;
import com.truward.polymer.printer.CAlikePrinter;

import javax.annotation.Nonnull;
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
    private final ParentProvider parents;
    private final CAlikePrinter printer;
    @Nonnull private JstVisitor<IOException> visitor;

    private void setVisitor(@Nonnull JstVisitor<IOException> visitor) {
      this.visitor = visitor;
    }

    private PrintVisitor(@Nonnull ParentProvider parents, @Nonnull CAlikePrinter printer) {
      this.parents = parents;
      this.printer = printer;
      setVisitor(this);
    }

    @Override
    public void visitUnit(@Nonnull Jst.Unit node) throws IOException {
      printer.print("package").print(' ').print(node.getPackageName()).print(';');

      if (!node.getImports().isEmpty()) {
        printer.print('\n');
        for (final Jst.Import importNode : node.getImports()) {
          visitImport(importNode);
        }
      }

      if (!node.getClasses().isEmpty()) {
        printer.print('\n');
        for (final Jst.ClassDeclaration classDeclaration : node.getClasses()) {
          visitClass(classDeclaration);
        }
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
        printNamedStatement(node, false);
        if (node.getFlags().contains(JstFlag.INTERFACE)) {
          printer.print("interface");
        } else if (node.getFlags().contains(JstFlag.ENUM)) {
          printer.print("enum");
        } else {
          printer.print("class");
        }

        printer.print(' ').print(node.getName());

        // print type parameters
        if (!node.getTypeParameters().isEmpty()) {
          printer.print(' ').print('<');
          printCommaSeparated(node.getTypeParameters());
          printer.print('>');
        }

        // print superclass
        final Jst.TypeExpression superclass = node.getSuperclass();
        if (superclass != null) {
          assert !node.getFlags().contains(JstFlag.INTERFACE) : "Interface should not have superclass";
          printer.print(' ').print("extends").print(' ');
          superclass.accept(visitor);
        }

        // print interfaces
        if (!node.getInterfaces().isEmpty()) {
          if (node.getFlags().contains(JstFlag.INTERFACE)) {
            printer.print("extends");
          } else {
            printer.print("implements");
          }

          printCommaSeparated(node.getInterfaces());
        }
      }

      printer.print(' ');
      node.getBody().accept(visitor);
    }

    @Override
    public void visitMethod(@Nonnull Jst.MethodDeclaration node) throws IOException {
      printNamedStatement(node, false);
      node.getReturnType().accept(visitor);
      printer.print(' ').print(node.getName());
      printer.print('(');
      printCommaSeparated(node.getArguments());
      printer.print(')');
    }

    @Override
    public void visitVar(@Nonnull Jst.VarDeclaration node) throws IOException {
      boolean isInBlock = parents.getParent() instanceof Jst.Block; // parent is a block (variable or field)

      printNamedStatement(node, !isInBlock); // inline annotations if not in block (e.g. for arguments)
      node.getType().accept(visitor);
      printer.print(' ').print(node.getName());

      final Jst.Expression initializer = node.getInitializer();
      if (initializer != null) {
        printer.print(' ').print('=').print(' ');
        initializer.accept(visitor);
      }

      if (isInBlock) {
        printer.print(';');
      }
    }

    @Override
    public void visitBlock(@Nonnull Jst.Block node) throws IOException {
      printer.print('{');
      for (final Jst.Statement statement : node.getStatements()) {
        statement.accept(visitor);
      }
      printer.print('}');
    }

    //
    // Private
    //

    private void printCommaSeparated(@Nonnull List<? extends Jst.Node> nodes) throws IOException {
      final int size = nodes.size();
      for (int i = 0; i < size; ++i) {
        if (i > 0) {
          printer.print(',').print(' ');
        }
        nodes.get(i).accept(visitor);
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

    private void print(@Nonnull List<Jst.Statement> statements) throws IOException {
      for (final Jst.Statement statement : statements) {
        statement.accept(visitor);
      }
    }
  }
}
