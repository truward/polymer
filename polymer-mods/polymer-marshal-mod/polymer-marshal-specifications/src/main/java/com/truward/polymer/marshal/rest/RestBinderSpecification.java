package com.truward.polymer.marshal.rest;

import com.truward.polymer.annotation.SpecificatorInvocation;

/**
 * Designates reaction on certain rest method.
 *
 * @author Alexander Shabanov
 */
public interface RestBinderSpecification {
  RestBinderSpecification trigger(@SpecificatorInvocation Object invocation);

  /**
   * This is a counterpart of {@link #trigger(Object)} method for invocations that return void.
   * It is expected, that followup call will be handled by specification service, e.g.
   * <code>
   *   specificationService.on(POST, "/method").triggerNextCall();
   *   exposedService.invokeVoidMethod(specificationService.body(BodyMessage.class));
   * </code>
   *
   */
  void triggerNextCall();
}
