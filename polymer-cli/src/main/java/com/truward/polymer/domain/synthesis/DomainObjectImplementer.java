package com.truward.polymer.domain.synthesis;

import com.truward.polymer.core.output.OutputStreamProvider;
import com.truward.polymer.domain.analysis.DomainImplementationTarget;
import com.truward.polymer.domain.analysis.DomainImplementerSettingsReader;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Generates a code that corresponds to the particular implementation targets
 *
 * @author Alexander Shabanov
 */
public interface DomainObjectImplementer {

  void generateCode(@Nonnull List<DomainImplementationTarget> implTargets);
}
