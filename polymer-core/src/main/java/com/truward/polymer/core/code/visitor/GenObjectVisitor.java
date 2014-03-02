package com.truward.polymer.core.code.visitor;

import com.truward.polymer.core.code.GenObject;
import com.truward.polymer.core.code.typed.GenArray;
import com.truward.polymer.core.code.typed.GenClass;
import com.truward.polymer.core.code.typed.GenParameterizedType;
import com.truward.polymer.core.code.untyped.GenChar;
import com.truward.polymer.core.code.untyped.GenFqNamed;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.code.untyped.GenString;

import javax.annotation.Nonnull;

/**
 * Represents abstract object visitor
 *
 * @author Alexander Shabanov
 */
public abstract class GenObjectVisitor <T extends Exception> {
  public void visitObject(@Nonnull GenObject o) throws T {
    throw new IllegalStateException("Undefined visitor method for object " + o);
  }

  public void visitChar(@Nonnull GenChar o) throws T {
    visitObject(o);
  }

  public void visitString(@Nonnull GenString o) throws T {
    visitObject(o);
  }

  public void visitFqNamed(@Nonnull GenFqNamed o) throws T {
    visitObject(o);
  }

  public void visitInlineBlock(@Nonnull GenInlineBlock o) throws T {
    visitObject(o);
  }

  public void visitArray(@Nonnull GenArray o) throws T {
    visitObject(o);
  }

  public void visitClass(@Nonnull GenClass o) throws T {
    visitObject(o);
  }

  public void visitParameterizedType(@Nonnull GenParameterizedType o) throws T {
    visitObject(o);
  }



  public static <T extends Exception> void apply(@Nonnull GenObject o, @Nonnull GenObjectVisitor<T> visitor) throws T {
    if (o instanceof GenChar) {
      visitor.visitChar((GenChar) o);
    } else if (o instanceof GenString) {
      visitor.visitString((GenString) o);
    } else if (o instanceof GenFqNamed) {
      visitor.visitFqNamed((GenFqNamed) o);
    } else if (o instanceof GenInlineBlock) {
      visitor.visitInlineBlock((GenInlineBlock) o);
    } else if (o instanceof GenArray) {
      visitor.visitArray((GenArray) o);
    } else if (o instanceof GenClass) {
      visitor.visitClass((GenClass) o);
    } else if (o instanceof GenParameterizedType) {
      visitor.visitParameterizedType((GenParameterizedType) o);
    } else {
      visitor.visitObject(o);
    }
  }
}
