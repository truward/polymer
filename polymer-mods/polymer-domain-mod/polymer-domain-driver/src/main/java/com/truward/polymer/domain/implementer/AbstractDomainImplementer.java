package com.truward.polymer.domain.implementer;

import com.truward.polymer.core.code.builder.CodeStream;
import com.truward.polymer.core.code.builder.CodeStreamSupport;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.support.GenDomainClass;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public abstract class AbstractDomainImplementer extends CodeStreamSupport {
  private final CodeStream codeStream;
  private final GenDomainClass domainClass;

  protected AbstractDomainImplementer(@Nonnull CodeStream codeStream, @Nonnull GenDomainClass domainClass) {
    if (!domainClass.isFrozen()) {
      throw new IllegalStateException("Domain class is not frozen");
    }

    this.codeStream = codeStream;
    this.domainClass = domainClass;
  }

  @Nonnull
  public final GenDomainClass getDomainClass() {
    return domainClass;
  }

  @Nonnull
  @Override
  protected final CodeStream getRootCodeStream() {
    return codeStream;
  }

  @Nonnull
  public final Class<?> getOriginClass() {
    return getDomainClass().getOrigin().getOriginClass();
  }

  @Nonnull
  public final DomainAnalysisResult getAnalysisResult() {
    return getDomainClass().getOrigin();
  }

  @Nonnull
  public final CodeStreamSupport field(@Nonnull DomainField field, @Nonnull List<Modifier> mods) {
    return this.var(field.getFieldType(), field.getFieldName(), mods).c(';');
  }

  @Nonnull
  public final CodeStreamSupport field(@Nonnull DomainField field, @Nonnull Modifier... mods) {
    return field(field, Arrays.asList(mods));
  }
}
