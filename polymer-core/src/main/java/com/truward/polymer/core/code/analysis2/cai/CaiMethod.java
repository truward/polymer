package com.truward.polymer.core.code.analysis2.cai;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Alexander Shabanov
 */
public interface CaiMethod extends CaiAnnotated {

    @Nonnull
    String getName();

    @Nonnull
    Type getReturnType();

    @Nonnull
    Collection<CaiVar> getArguments();

    boolean isConstructor();

    @Nonnull
    CaiMethodRole getMethodRole();

    void setMethodRole(@Nonnull CaiMethodRole role);
}
