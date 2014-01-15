package com.truward.polymer.domain.synthesis;

import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Generates a code that corresponds to the particular implementation targets
 *
 * @author Alexander Shabanov
 */
public interface DomainObjectImplementer {

  void generateCode(@Nonnull OutputStreamProvider outputStreamProvider,
                    @Nonnull DomainImplementerSettingsReader implementerSettings,
                    @Nonnull List<DomainAnalysisResult> implTargets);
}
