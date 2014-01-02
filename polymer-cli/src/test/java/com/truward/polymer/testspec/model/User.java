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

  public interface Builder {
    Builder setId(Long id);
    Builder setName(String name);
    Builder addRoles(List<UserRole> roles);
    Builder setRatings(List<Integer> ratings);
    Builder setNicknames(String[] nicknames);

    User build();
  }
}
