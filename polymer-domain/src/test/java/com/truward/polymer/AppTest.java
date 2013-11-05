package com.truward.polymer;

import com.truward.polymer.annotation.ProvidedService;
import com.truward.polymer.builder.BuilderSupport;
import com.truward.polymer.builder.GenericBuilder;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * Sample spring-driven test.
 */
public class AppTest {

  interface User {
    long getId();
    String getName();

    interface Builder extends GenericBuilder<User> {
      Builder setId(long id);
      Builder setName(String name);
    }
  }

  @ProvidedService(
      targetPackage = "com.truward.polymer.generated",
      options = {"Serializable"},
      dtoClassTraits = {
          @ProvidedService.DtoClassTrait(target = User.class, implemented = Serializable.class)
      }
  )
  interface UserProvider extends BuilderSupport<User, User.Builder> {
    User createUser(long id, String name);
  }

  /**
   * Sample test method.
   */
  @Test
  public void testDummy() {
    assertTrue(true);
  }
}
