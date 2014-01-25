package com.truward.polymer.marshal;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Sample spring-driven test.
 */
public class DummyTest {

  @Test
  public void shouldPass() {
    assertTrue(true);
  }


  public interface Zoo {
    String getName();
    List<Animal> getAnimals();
  }

  public interface Animal {
    String getName();
    int getCount();
  }
}
