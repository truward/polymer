package com.truward.polymer.marshal.rest;

/**
 * Provides specification of the exposed methods, this is the most top level specification of the exposed functionality
 *
 * @author Alexander Shabanov
 */
public interface RestSpecifier {
  RestSettings getSettings();

  <T> T param(Class<T> paramClass);

  RestBinderSpecification on(HttpMethod method, String path);

  <T> T body(Class<T> bodyClass);
}
