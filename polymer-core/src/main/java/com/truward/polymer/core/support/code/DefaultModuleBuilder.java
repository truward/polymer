package com.truward.polymer.core.support.code;

import com.truward.polymer.core.code.builder.ModuleBuilder;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.freezable.FreezableSupport;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class DefaultModuleBuilder extends FreezableSupport implements ModuleBuilder {
  private final FqName targetClassName;
  private final GenInlineBlock codeStream;
  private final TypeManager typeManager;
  private final GenInlineBlock imports;

  public DefaultModuleBuilder(@Nonnull FqName targetClassName, @Nonnull TypeManager typeManager) {
    this.targetClassName = targetClassName;
    this.typeManager = typeManager;
    this.codeStream = new DefaultInlineBlock(typeManager);
    this.imports = new DefaultInlineBlock();

    // add package declaration at the very beginning
    if (!targetClassName.isRoot()) {
      this.codeStream.s("package").sp().s(targetClassName.getParent()).c(';').eol();
    }

    // add imports code marker
    this.codeStream.obj(this.imports);
  }

  @Override
  public GenInlineBlock getStream() {
    return codeStream;
  }

  @Override
  protected void setFrozen() {
    // set current package to the type manager
    typeManager.setPackageName(targetClassName);

    // freeze type manager, that will trigger preparation of the types
    typeManager.freeze();

    // insert imports
    final List<FqName> importNames = typeManager.getImportNames();
    for (final FqName importName : importNames) {
      this.imports.s("import").sp().s(importName).c(';');
    }
    this.imports.eol();

    this.codeStream.freeze();
    this.imports.freeze();
    super.setFrozen();
  }
}
