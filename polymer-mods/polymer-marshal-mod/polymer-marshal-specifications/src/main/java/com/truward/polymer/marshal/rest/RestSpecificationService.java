package com.truward.polymer.marshal.rest;

/**
 * Provides specification of the exposed methods, this is the most top level specification of the exposed functionality
 *
 * @author Alexander Shabanov
 */
public interface RestSpecificationService {

  <T> T param(Class<T> paramClass);

  RestBinderSpecification on(String path, HttpMethod method);

  <T> T body(Class<T> bodyClass);
}
