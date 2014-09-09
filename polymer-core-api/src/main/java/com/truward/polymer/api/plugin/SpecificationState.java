package com.truward.polymer.api.plugin;

/**
 * @author Alexander Shabanov
 */
public enum SpecificationState {

  /** Indicates, that specification processor is about to start processing the next specification class */
  START,

  /** Indicates, that specification processor is recording current specification */
  RECORDING,

  /** Indicates, that another specification has been submitted to the specification processor */
  SUBMITTED,

  /** Indicates, that all the specifications have been processed and submitted to the specification processors */
  COMPLETED
}
