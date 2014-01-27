package com.truward.polymer.marshal;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.marshal.rest.HttpMethod;
import com.truward.polymer.marshal.rest.RestSpecificationService;
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
  public void restApiAssigner(RestSpecificationService ss, @DomainObject ExposedService service) {
    ss.on("/animal/{id}", HttpMethod.GET).trigger(service.getAnimal(ss.param(Long.class)));

    // for void return types
    ss.on("/animal", HttpMethod.POST).triggerVoid();
    service.createAnimal(ss.body(Animal.class));
  }

  public interface ExposedService {
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
