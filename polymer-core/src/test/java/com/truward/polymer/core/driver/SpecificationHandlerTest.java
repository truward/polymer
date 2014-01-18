package com.truward.polymer.core.driver;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.core.PolymerModule;
import com.truward.polymer.core.output.OutputStreamProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Alexander Shabanov
 */
public class SpecificationHandlerTest {
  private SpecificationHandler specificationHandler;


  @Before
  public void init() {
    final PolymerModule module = new PolymerModule().addDefaults();

    module.getInjectionContext().registerBean(Mockito.mock(OutputStreamProvider.class, new Answer() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        throw new AssertionError("Should not be called");
      }
    }));

    specificationHandler = module.getInjectionContext().getBean(SpecificationHandler.class);
  }

  @Test
  public void shouldGenerateCode() {
    specificationHandler.parseClass(TestSpec.class);
    specificationHandler.done();
  }

  //
  // Test data, should be public
  //

  public static final class TestSpec {

    @Specification
    public void nullSpec() {
    }
  }
}
