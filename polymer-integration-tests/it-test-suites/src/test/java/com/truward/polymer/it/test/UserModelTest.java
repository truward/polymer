package com.truward.polymer.it.test;

import com.truward.polymer.generated.model.UserImpl;
import com.truward.polymer.it.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserModelTest {
  final Long id = 1L;
  final String name = "name";
  final int age = 12;

  final User user = UserImpl.newBuilder()
      .setId(id)
      .setName(name)
      .setAge(age)
      .build();

  @Test
  public void shouldMatchDefaultFields() {
    assertEquals(id, user.getId());
    assertEquals(name, user.getName());
    assertEquals(age, user.getAge());

    //noinspection ObjectEqualsNull
    assertFalse(user.equals(null));

    assertTrue(user.equals(user));

    // equals checks
    assertTrue(user.equals(UserImpl.newBuilder()
        .setId(id)
        .setName(name)
        .setAge(age)
        .build()));

    assertFalse(user.equals(UserImpl.newBuilder()
        .setId(id + 1)
        .setName(name)
        .setAge(age)
        .build()));
    assertFalse(user.equals(UserImpl.newBuilder()
        .setId(id)
        .setName(name + "a")
        .setAge(age)
        .build()));
    assertFalse(user.equals(UserImpl.newBuilder()
        .setId(id)
        .setName(name)
        .setAge(age + 1)
        .build()));
  }
}
