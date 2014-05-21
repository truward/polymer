package com.truward.polymer.core.code.analysis2.cai;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface CaiVisitor {

    void visitNode(@Nonnull CaiNode node);

    void visitField(@Nonnull CaiField node);

    void visitInterface(@Nonnull CaiInterface node);

    void visitMethod(@Nonnull CaiMethod node);

    void visitMethodRole(@Nonnull CaiMethodRole node);
}
