package com.truward.polymer.core.support.code;

import com.truward.polymer.core.code.builder.ModuleBuilder;
import com.truward.polymer.core.code.builder.TypeManager;
import com.truward.polymer.core.code.untyped.GenInlineBlock;
import com.truward.polymer.core.freezable.FreezableSupport;
import com.truward.polymer.naming.FqName;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class DefaultModuleBuilder extends FreezableSupport implements ModuleBuilder {
  private final FqName packageName;
  private final GenInlineBlock codeStream;
  private final TypeManager typeManager;
  private final GenInlineBlock imports;

  public DefaultModuleBuilder(@Nonnull FqName packageName, @Nonnull TypeManager typeManager) {
    this.packageName = packageName;
    this.typeManager = typeManager;
    this.codeStream = new DefaultInlineBlock(typeManager);

    this.codeStream.s("package").sp().s(packageName).c(';').eol();
    this.imports = new DefaultInlineBlock();
    this.codeStream.obj(this.imports);
  }

  @Override
  public GenInlineBlock getStream() {
    return codeStream;
  }

  @Override
  protected void setFrozen() {
    // set current package to the type manager
    typeManager.setPackageName(packageName);

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
