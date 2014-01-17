package com.truward.polymer.domain.analysis.support;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.code.naming.FqName;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainImplementationTarget;
import com.truward.polymer.domain.analysis.DomainImplementationTargetProvider;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Prepares domain implementation targets.
 *
 * @author Alexander Shabanov
 */
public final class DefaultDomainImplementationTargetProvider implements DomainImplementationTargetProvider, SpecificationStateAware {
  @Resource
  private DomainImplementerSettingsReader implementerSettings;

  private List<DomainImplementationTarget> implementationTargets = new ArrayList<>();

  @Nonnull
  @Override
  public List<DomainImplementationTarget> getImplementationTargets() {
    return ImmutableList.copyOf(implementationTargets);
  }

  @Override
  public void submit(@Nonnull DomainAnalysisResult analysisResult) {
    implementationTargets.add(new DefaultDomainImplementationTarget(analysisResult));
  }

  @Override
  public void setState(@Nonnull SpecificationState state) {
    if (state == SpecificationState.COMPLETED) {
      onCompleted();
    }
  }

  //
  // Private
  //

  private void onCompleted() {
    for (final DomainImplementationTarget target : implementationTargets) {
      target.setTargetName(getTargetClassName(target.getAnalysisResult()));
    }
  }

  private FqName getTargetClassName(@Nonnull DomainAnalysisResult result) {
    final StringBuilder nameBuilder = new StringBuilder(200);
    nameBuilder.append(implementerSettings.getDefaultTargetPackageName()).append('.');
    nameBuilder.append(implementerSettings.getDefaultImplClassPrefix())
        .append(result.getOriginClass().getSimpleName())
        .append(implementerSettings.getDefaultImplClassSuffix());

    return FqName.parse(nameBuilder.toString());
  }
}
