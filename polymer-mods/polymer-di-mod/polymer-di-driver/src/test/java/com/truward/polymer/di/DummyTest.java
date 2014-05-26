package com.truward.polymer.di;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.specification.annotation.Specification;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public final class DummyTest {

  @Test
  public void shouldPass() {
    assertTrue(true);
  }


  // [1] Sample - application

  @Specification
  public void specifyContext(ContextSpecification contextSpecification) {
    contextSpecification.bind(ZooService.class).to(DefaultZooService.class);
    contextSpecification.bind(Runnable.class).to(AppRunner.class);
  }

  // sample usage
  public static void runSampleApp() {
    Context c = create(Context.class); // TBD: specify binding
    c.getAppRunner().run();
  }


  private interface Context {
    Runnable getAppRunner();
    /*Runnable getAppRunner(args/properties/settings);*/
  }

  private static final class AppRunner implements Runnable {
    private final ZooService zooService;

    public AppRunner(ZooService zooService) {
      this.zooService = zooService;
    }

    @Override
    public void run() {
    }
  }

  private interface ZooService {
    List<Animal> getAnimals();
  }

  private static final class DefaultZooService implements ZooService {

    @Override
    public List<Animal> getAnimals() {
      return ImmutableList.of();
    }
  }

  private interface Animal {
    int getId();
    String getName();
  }

  //
  // Prototype interfaces
  //

  public interface ContextSpecification {
    <T> ContextClassBinder<T> bind(Class<T> boundInterface);
  }

  public interface ContextClassBinder<T> {
    ContextClassBinder<T> to(Class<? extends T> boundClass);
  }

  public static <T> T create(Class<T> clazz) {
    throw new UnsupportedOperationException(); // cheat the compiler
  }
}
