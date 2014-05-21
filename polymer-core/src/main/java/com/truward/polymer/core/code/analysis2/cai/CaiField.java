package com.truward.polymer.core.code.analysis2.cai;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

/**
 * @author Alexander Shabanov
 */
public interface CaiField extends CaiAnnotated {

    @Nonnull
    Type getFieldType();
}
