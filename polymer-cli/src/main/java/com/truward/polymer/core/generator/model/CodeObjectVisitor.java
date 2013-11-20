package com.truward.polymer.core.generator.model;

import java.util.List;

/**
 * @author Alexander Shabanov
 */
public abstract class CodeObjectVisitor {
  public void visitObject(Object obj) {
    throw new IllegalStateException("Object is not visited: " + obj);
  }

  public void visitChar(char obj) {
    visitObject(obj);
  }

  public void visitCharSequence(CharSequence obj) {
    visitObject(obj);
  }

  public void visitList(List<?> obj) {
    visitObject(obj);
  }

  public void visitSingleLineComment(SingleLineComment obj) {
    visitObject(obj);
  }

  public void visitCommentBlock(CommentBlock obj) {
    visitObject(obj);
  }

  public static void apply(CodeObjectVisitor visitor, Object obj) {
    if (obj instanceof Character) {
      visitor.visitChar((Character) obj);
    } else if (obj instanceof CharSequence) {
      visitor.visitCharSequence((CharSequence) obj);
    } else if (obj instanceof List) {
      visitor.visitList((List) obj);
    } else if (obj instanceof Text) {
      visitor.visitCharSequence(((Text) obj).getText());
    } else if (obj instanceof CommentBlock) {
      visitor.visitCommentBlock((CommentBlock) obj);
    } else if (obj instanceof SingleLineComment) {
      visitor.visitSingleLineComment((SingleLineComment) obj);
    } else {
      visitor.visitObject(obj);
    }
  }
}
