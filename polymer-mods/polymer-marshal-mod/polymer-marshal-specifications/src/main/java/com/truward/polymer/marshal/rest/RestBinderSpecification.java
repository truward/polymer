package com.truward.polymer.marshal.rest;

import com.truward.polymer.annotation.SpecificatorInvocation;

/**
 * Designates reaction on certain rest method.
 *
 * @author Alexander Shabanov
 */
public interface RestBinderSpecification {
  RestBinderSpecification trigger(@SpecificatorInvocation Object invocation);

  void triggerVoid();
}
