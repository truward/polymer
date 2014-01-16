package com.truward.polymer.testspec.model;

import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface User {
  Long getId();
  String getName();
  List<UserRole> getRoles();
  List<Integer> getRatings();
  String[] getNicknames();
}
