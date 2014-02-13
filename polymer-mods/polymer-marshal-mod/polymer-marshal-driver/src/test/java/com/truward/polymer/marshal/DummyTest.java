package com.truward.polymer.marshal;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.marshal.rest.HttpMethod;
import com.truward.polymer.marshal.rest.RestSpecifier;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Sample spring-driven test.
 */
public class DummyTest {

  @Test
  public void shouldPass() {
    assertTrue(true);
  }

  @Specification
  public void restApiAssigner(RestSpecifier ss, @Exposed AnimalService service) {
    ss.getSettings().setBasePath("/zoo");

    ss.on(HttpMethod.GET, "/animal/{id}").trigger(service.getAnimal(ss.param(Long.class)));

    // for void return types
    ss.on(HttpMethod.POST, "/animal").triggerNextCall();
    service.createAnimal(ss.body(Animal.class));
  }

  public interface AnimalService {
    Animal getAnimal(long id);

    void createAnimal(Animal animal);
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
